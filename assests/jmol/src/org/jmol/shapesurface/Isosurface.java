/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-04-25 11:08:02 -0500 (Wed, 25 Apr 2007) $
 * $Revision: 7492 $
 *
 * Copyright (C) 2005 Miguel, Jmol Development
 *
 * Contact: jmol-developers@lists.sf.net,jmol-developers@lists.sourceforge.net
 * Contact: hansonr@stolaf.edu
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

/*
 * miguel 2005 07 17
 *
 *  System and method for the display of surface structures
 *  contained within the interior region of a solid body
 * United States Patent Number 4,710,876
 * Granted: Dec 1, 1987
 * Inventors:  Cline; Harvey E. (Schenectady, NY);
 *             Lorensen; William E. (Ballston Lake, NY)
 * Assignee: General Electric Company (Schenectady, NY)
 * Appl. No.: 741390
 * Filed: June 5, 1985
 *
 *
 * Patents issuing prior to June 8, 1995 can last up to 17
 * years from the date of issuance.
 *
 * Dec 1 1987 + 17 yrs = Dec 1 2004
 */

/*
 * Bob Hanson May 22, 2006
 * 
 * implementing marching squares; see 
 * http://www.secam.ex.ac.uk/teaching/ug/studyres/COM3404/COM3404-2006-Lecture15.pdf
 *  
 * inventing "Jmol Voxel File" format, *.jvxl
 * 
 * see http://www.stolaf.edu/academics/chemapps/jmol/docs/misc/JVXL-format.pdf
 * 
 * lines through coordinates are identical to CUBE files
 * after that, we have a line that starts with a negative number to indicate this
 * is a JVXL file:
 * 
 * line1:  (int)-nSurfaces  (int)edgeFractionBase (int)edgeFractionRange  
 * (nSurface lines): (float)cutoff (int)nBytesData (int)nBytesFractions
 * 
 * definition1
 * edgedata1
 * fractions1
 * colordata1
 * ....
 * definition2
 * edgedata2
 * fractions2
 * colordata2
 * ....
 * 
 * definitions: a line with detail about what sort of compression follows
 * 
 * edgedata: a list of the count of vertices ouside and inside the cutoff, whatever
 * that may be, ordered by nested for loops for(x){for(y){for(z)}}}.
 * 
 * nOutside nInside nOutside nInside...
 * 
 * fractions: an ascii list of characters represting the fraction of distance each
 * encountered surface point is along each voxel cube edge found to straddle the 
 * surface. The order written is dictated by the reader algorithm and is not trivial
 * to describe. Each ascii character is constructed by taking a base character and 
 * adding onto it the fraction times a range. This gives a character that can be
 * quoted EXCEPT for backslash, which MAY be substituted for by '!'. Jmol uses the 
 * range # - | (35 - 124), reserving ! and } for special meanings.
 * 
 * colordata: same deal here, but with possibility of "double precision" using two bytes.
 * 
 */

package org.jmol.shapesurface;

import org.jmol.shape.Mesh;
import org.jmol.shape.MeshCollection;
import org.jmol.shape.Shape;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;

import org.jmol.util.ColorEncoder;
import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.util.Parser;
import org.jmol.util.Point3fi;
import org.jmol.util.TextFormat;
import org.jmol.viewer.ActionManager;
import org.jmol.viewer.JmolConstants;
import org.jmol.script.Token;
import org.jmol.viewer.Viewer;
import org.jmol.viewer.StateManager.Orientation;
import org.jmol.jvxl.readers.Parameters;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.jvxl.api.MeshDataServer;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.data.JvxlData;
import org.jmol.jvxl.data.MeshData;
import org.jmol.jvxl.readers.SurfaceGenerator;

public class Isosurface extends MeshCollection implements MeshDataServer {

  private IsosurfaceMesh[] isomeshes = new IsosurfaceMesh[4];
  protected IsosurfaceMesh thisMesh;

  public void allocMesh(String thisID, Mesh m) {
    int index = meshCount++;
    meshes = isomeshes = (IsosurfaceMesh[]) ArrayUtil.ensureLength(isomeshes,
        meshCount * 2);
    currentMesh = thisMesh = isomeshes[index] = (m == null ? new IsosurfaceMesh(
        thisID, g3d, colix, index) : (IsosurfaceMesh) m);
    currentMesh.index = index;
    sg.setJvxlData(jvxlData = thisMesh.jvxlData);
  }

  public void initShape() {
    super.initShape();
    myType = "isosurface";
    newSg();
  }

  private void newSg() {
    sg = new SurfaceGenerator(viewer, this, colorEncoder, null, jvxlData = new JvxlData());
    sg.setVersion("Jmol " + Viewer.getJmolVersion());
  }
  
  protected void clearSg() {
    sg = null; // not Molecular Orbitals
  }
  //private boolean logMessages;
  private int lighting;
  private boolean iHaveBitSets;
  private boolean explicitContours;
  private int atomIndex;
  private int moNumber;
  private short defaultColix;
  private short meshColix;
  private Point3f center;
  private Point3f offset;
  private float scale3d;
  private boolean isPhaseColored;
  private boolean isColorExplicit;

  protected SurfaceGenerator sg;
  protected JvxlData jvxlData;

  private ColorEncoder colorEncoder = new ColorEncoder();
  private float withinDistance;
  private Vector withinPoints;

  public void setProperty(String propertyName, Object value, BitSet bs) {

    // //isosurface-only (no calculation required; no calculation parameters to
    // set)

    if ("navigate" == propertyName) {
      navigate(((Integer) value).intValue());
      return;
    }
    if ("delete" == propertyName) {
      setPropertySuper(propertyName, value, bs);
      if (!explicitID)
        nLCAO = nUnnamed = 0;
      return;
    }

    if ("remapcolor" == propertyName) {
      if (thisMesh != null) {
        Object[] o = (Object[]) value;
        remapColors((String) o[0], ((Boolean) o[1]).booleanValue(), (float[]) o[2]);
      }
      return;
    }

    if ("thisID" == propertyName) {
      if (actualID != null)
        value = actualID;
      setPropertySuper("thisID", value, null);
      return;
    }

    if ("map" == propertyName) {
      setProperty("squareData", Boolean.FALSE, null);
      return;
    }

    if ("color" == propertyName) {
      if (thisMesh != null) {
        // thisMesh.vertexColixes = null;
        thisMesh.isColorSolid = true;
        thisMesh.polygonColixes = null;
      } else if (!TextFormat.isWild(previousMeshID)) {
        for (int i = meshCount; --i >= 0;) {
          // isomeshes[i].vertexColixes = null;
          isomeshes[i].isColorSolid = true;
          isomeshes[i].polygonColixes = null;
        }
      }
      setPropertySuper(propertyName, value, bs);
      return;
    }

    if ("fixed" == propertyName) {
      isFixed = ((Boolean) value).booleanValue();
      setModelIndex();
      return;
    }

    if ("modelIndex" == propertyName) {
      if (!iHaveModelIndex) {
        modelIndex = ((Integer) value).intValue();
        isFixed = (modelIndex < 0);
        sg.setModelIndex(Math.abs(modelIndex));
      }
      return;
    }

    if ("lcaoCartoon" == propertyName || "lonePair" == propertyName
        || "radical" == propertyName) {
      // z x center rotationAxis (only one of x, y, or z is nonzero; in radians)
      Vector3f[] info = (Vector3f[]) value;
      if (!explicitID) {
        setPropertySuper("thisID", null, null);
      }
      // center (info[2]) is set in SurfaceGenerator
      if (!sg.setParameter("lcaoCartoonCenter", info[2]))
        drawLcaoCartoon(
            info[0],
            info[1],
            info[3],
            ("lonePair" == propertyName ? 2 : "radical" == propertyName ? 1 : 0));
      return;
    }

    if ("select" == propertyName) {
      if (iHaveBitSets)
        return;
    }

    if ("ignore" == propertyName) {
      if (iHaveBitSets)
        return;
    }

    if ("colorMesh" == propertyName) {
      int rgb = ((Integer) value).intValue();
      meshColix = Graphics3D.getColix(rgb);
      return;
    }

    if ("offset" == propertyName) {
      offset = new Point3f((Point3f) value);
      if (offset.equals(JmolConstants.center))
        offset = null;
      if (thisMesh != null) {
        thisMesh.ptOffset = offset;
        thisMesh.offsetVertices = null;
      }
      return;
    }

    // Isosurface / SurfaceGenerator both interested

    if ("setColorScheme" == propertyName) {
      Object[] o = (Object[]) value;
      boolean isTranslucent = ((Boolean) o[1]).booleanValue();
      if (thisMesh != null) {
        thisMesh.colix = Graphics3D.getColixTranslucent(thisMesh.colix,
            isTranslucent, isTranslucent ? 0.5f : 0);
      }
    }
    if ("title" == propertyName) {
      if (value instanceof String && "-".equals((String) value))
        value = null;
      setPropertySuper(propertyName, value, bs);
      value = title;
    }

    if ("withinPoints" == propertyName) {
      Object[] o = (Object[]) value;
      withinDistance = ((Float) o[0]).floatValue();
      BitSet bsAtoms = (BitSet) o[2];
      withinPoints = (Vector) o[3];
      if (withinPoints.size() == 0)
        withinPoints = viewer.getAtomPointVector(bsAtoms);
    }

    if ("scale3d" == propertyName) {
      scale3d = ((Float) value).floatValue();
      if (thisMesh != null) {
        thisMesh.scale3d = thisMesh.jvxlData.scale3d = scale3d;
        thisMesh.offsetVertices = null;
      }
    }

    if ("getSurfaceSets" == propertyName) {
      if (thisMesh != null)
        thisMesh.thisSet = ((Integer) value).intValue();
    }

    if ("contour" == propertyName) {
      explicitContours = true;
    }

    if ("atomIndex" == propertyName) {
      atomIndex = ((Integer) value).intValue();
    }

    if ("pocket" == propertyName) {
      Boolean pocket = (Boolean) value;
      lighting = (pocket.booleanValue() ? JmolConstants.FULLYLIT
          : JmolConstants.FRONTLIT);
    }

    if ("colorRGB" == propertyName) {
      int rgb = ((Integer) value).intValue();
      defaultColix = Graphics3D.getColix(rgb);
    }

    if ("molecularOrbital" == propertyName) {
      moNumber = ((Integer) value).intValue();
      if (!isColorExplicit)
        isPhaseColored = true;
    }

    if (propertyName == "functionXY") {
      if (sg.isStateDataRead())
        setScriptInfo(null); // for script DATA1
    }

    if ("center" == propertyName) {
      center.set((Point3f) value);
    }

    if ("phase" == propertyName) {
      isPhaseColored = true;
    }

    if ("plane" == propertyName) {
      allowContourLines = false;
    }

    if ("functionXY" == propertyName) {
      allowContourLines = false;
    }

    if ("finalize" == propertyName) {
      thisMesh.setDiscreteColixes(sg.getParams().contoursDiscrete, sg
          .getParams().contourColixes);
      setScriptInfo((String) value);
      setJvxlInfo();
      clearSg();
      return;
    }

    if ("init" == propertyName) {
      newSg();
    }

    if ("localName" == propertyName) {
      value = viewer.getOutputStream((String) value, null);
      propertyName = "outputStream";
    }

    if ("mapColor" == propertyName || "readFile" == propertyName) {
      if (value == null) {
        // ScriptEvaluator has passed the filename to us as the value of the
        // "fileName" property. We retrieve that from the surfaceGenerator
        // and open a BufferedReader for it. Or not. But that would be
        // unlikely since we have just checked it in ScriptEvaluator
        value = viewer.getBufferedReaderOrErrorMessageFromName(
            sg.getFileName(), null, true);
        if (value instanceof String) {
          Logger.error("Isosurface: could not open file " + sg.getFileName()
              + " -- " + value);
          return;
        }
        try {
          value = new BufferedReader(new InputStreamReader((InputStream) value, "ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
          // ignore
        }

      }
    }
    // surface Export3D only (return TRUE) or shared (return FALSE)

    if (sg != null && sg.setParameter(propertyName, value, bs))
      return;

    // ///////////// isosurface LAST, shared

    if ("init" == propertyName) {
      explicitID = false;
      String script = (value instanceof String ? (String) value : null);
      int pt = (script == null ? -1 : script.indexOf("# ID="));
      actualID = (pt >= 0 ? Parser.getNextQuotedString(script, pt) : null);
      setPropertySuper("thisID", JmolConstants.PREVIOUS_MESH_ID, null);
      if (script != null && !(iHaveBitSets = getScriptBitSets(script, null)))
        sg.setParameter("select", bs);
      initializeIsosurface();
      sg.setModelIndex(isFixed ? -1 : modelIndex);
      return;
    }

    if ("clear" == propertyName) {
      discardTempData(true);
      return;
    }

    /*
     * if ("background" == propertyName) { boolean doHide = !((Boolean)
     * value).booleanValue(); if (thisMesh != null) thisMesh.hideBackground =
     * doHide; else { for (int i = meshCount; --i >= 0;)
     * meshes[i].hideBackground = doHide; } return; }
     */

    if (propertyName == "deleteModelAtoms") {
      int modelIndex = ((int[]) ((Object[]) value)[2])[0];
      BitSet bsModels = BitSetUtil.setBit(modelIndex);
      int firstAtomDeleted = ((int[]) ((Object[]) value)[2])[1];
      int nAtomsDeleted = ((int[]) ((Object[]) value)[2])[2];
      for (int i = meshCount; --i >= 0;) {
        Mesh m = meshes[i];
        if (m == null)
          continue;
        if (m.modelIndex == modelIndex) {
          meshCount--;
          if (m == currentMesh)
            currentMesh = thisMesh = null;
          meshes = isomeshes = (IsosurfaceMesh[]) ArrayUtil.deleteElements(
              meshes, i, 1);
        } else if (m.modelIndex > modelIndex) {
          m.modelIndex--;
          if (m.atomIndex >= firstAtomDeleted)
            m.atomIndex -= nAtomsDeleted;
          if (m.bitsets != null) {
            BitSetUtil.deleteBits(m.bitsets[0], bs);
            BitSetUtil.deleteBits(m.bitsets[1], bs);
            BitSetUtil.deleteBits(m.bitsets[2], bsModels);
          }
        }
      }
      return;
    }

    if ("setColorScheme" == propertyName) {
      String schemeName = ((String) ((Object[]) value)[0]);
      boolean isTranslucent = ((Boolean)((Object[]) value)[1]).booleanValue();
      setColorCommand(schemeName, isTranslucent);
      return;
    }

    // processing by meshCollection:
    setPropertySuper(propertyName, value, bs);
  }  

  private void setPropertySuper(String propertyName, Object value, BitSet bs) {
    if (propertyName == "thisID" && currentMesh != null 
        && currentMesh.thisID.equals((String) value)) {
      checkExplicit((String) value);
      return;
    }
    currentMesh = thisMesh;
    super.setProperty(propertyName, value, bs);
    thisMesh = (IsosurfaceMesh) currentMesh;
    jvxlData = (thisMesh == null ? null : thisMesh.jvxlData);
    if (sg != null)
      sg.setJvxlData(jvxlData);
  }

  public boolean getProperty(String property, Object[] data) {
    if (property == "intersectPlane") {
      IsosurfaceMesh mesh = (IsosurfaceMesh) getMesh((String) data[0]);
      if (mesh == null)
        return false;
      data[3] = new Integer(mesh.modelIndex);
      return mesh.getIntersection((Point4f) data[1], (Vector) data[2], false);
    }
    if (property == "getBoundingBox") {
      String id = (String) data[0];
      IsosurfaceMesh m = (IsosurfaceMesh) getMesh(id);
      if (m == null || m.vertices == null)
        return false;
      data[2] = m.jvxlData.boundingBox;
      return true;
    }
    if (property == "getCenter") {
      int index = ((Integer)data[1]).intValue();
      if (index < 0) {
        String id = (String) data[0];
        IsosurfaceMesh m = (IsosurfaceMesh) getMesh(id);
        if (m == null || m.vertices == null)
          return false;
        Point3f p = new Point3f(m.jvxlData.boundingBox[0]);
        p.add(m.jvxlData.boundingBox[1]);
        p.scale(0.5f);
        data[2] = p;
        return true;
      }
      // continue to super
    }

    return super.getProperty(property, data);
  }

  public Object getProperty(String property, int index) {
    Object ret = super.getProperty(property, index);
    if (ret != null)
      return ret;
    if (property == "dataRange")
      return (thisMesh == null || jvxlData.jvxlPlane != null && !jvxlData.jvxlDataIsColorMapped ? null : new float[] {
          jvxlData.mappedDataMin, jvxlData.mappedDataMax,
          (jvxlData.isColorReversed ? jvxlData.valueMappedToBlue : jvxlData.valueMappedToRed),
          (jvxlData.isColorReversed ? jvxlData.valueMappedToRed : jvxlData.valueMappedToBlue)});
    if (property == "moNumber")
      return new Integer(moNumber);
    if (property == "area")
      return (thisMesh == null ? new Float(Float.NaN) : thisMesh.calculateArea());
    if (property == "volume")
      return (thisMesh == null ? new Float(Float.NaN) : thisMesh.calculateVolume());
    if (thisMesh == null)
      return null;//"no current isosurface";
    if (property == "cutoff")
      return new Float(jvxlData.cutoff);
    if (property == "minMaxInfo")
      return new float[] { jvxlData.dataMin, jvxlData.dataMax };
    if (property == "plane")
      return jvxlData.jvxlPlane;
    if (property == "jvxlDataXml" || property == "jvxlMeshXml") {
      MeshData meshData = null;
      if (property == "jvxlMeshXml" || jvxlData.vertexDataOnly) {
        meshData = new MeshData();
        fillMeshData(meshData, MeshData.MODE_GET_VERTICES, null);
        meshData.polygonColorData = getPolygonColorData(meshData.polygonCount, meshData.polygonColixes);
      }
      return JvxlCoder.jvxlGetFile(jvxlData, meshData, title, "", true, 1, thisMesh
              .getState(myType), (thisMesh.scriptCommand == null ? "" : thisMesh.scriptCommand));
    }
    if (property == "jvxlFileInfo")
      return JvxlCoder.jvxlGetInfo(jvxlData);
    return null;
  }

  public static String getPolygonColorData(int ccount, short[] colixes) {
    if (colixes == null)
      return null;
    StringBuffer list1 = new StringBuffer();
    int count = 0;
    short colix = 0;
    boolean done = false;
    for (int i = 0; i < ccount || (done = true) == true; i++) {
      if (done || colixes[i] != colix) {
        if (count != 0)
          list1.append(" ").append(count).append(" ").append(
              (colix == 0 ? 0 : Graphics3D.getArgb(colix)));
        if (done)
          break;
        colix = colixes[i];
        count = 1;
      } else {
        count++;
      }
    }
    list1.append("\n");
    return list1.toString();
  }

  protected void getColorState(StringBuffer sb, Mesh mesh) {
    boolean colorArrayed = (mesh.isColorSolid && ((IsosurfaceMesh) mesh).polygonColixes != null);
    if (mesh.isColorSolid && !colorArrayed)
      appendCmd(sb, getColorCommand(myType, mesh.colix));  
  }
  
  private boolean getScriptBitSets(String script, BitSet[] bsCmd) {
    this.script = script;
    getModelIndex(script);
    if (script == null)
      return false;
    getCapSlabInfo(script);
    int i = script.indexOf("# ({");
    if (i < 0)
      return false;
    int j = script.indexOf("})", i);
    if (j < 0)
      return false;
    BitSet bs = Escape.unescapeBitset(script.substring(i + 2, j + 2));
    if (bsCmd == null)
      sg.setParameter("select", bs);
    else
      bsCmd[0] = bs;
    if ((i = script.indexOf("({", j)) < 0)
      return true;
    j = script.indexOf("})", i);
    if (j < 0) 
      return false;
      bs = Escape.unescapeBitset(script.substring(i, j + 2));
      if (bsCmd == null)
        sg.setParameter("ignore", bs);
      else
        bsCmd[1] = bs;
    if ((i = script.indexOf("/({", j)) == j + 2) {
      if ((j = script.indexOf("})", i)) < 0)
        return false;
      bs = Escape.unescapeBitset(script.substring(i + 1, j + 2));
      if (bsCmd == null)
        viewer.setTrajectory(bs);
      else
        bsCmd[2] = bs;
    }
    return true;
  }

  protected void getCapSlabInfo(String script) {
    int i = script.indexOf("# SLAB=");
    if (i >= 0)
      sg.setParameter("slab", getCapSlabObject(i, script));
    i = script.indexOf("# CAP=");
    if (i >= 0)
      sg.setParameter("cap", getCapSlabObject(i, script));
  }

  private Object getCapSlabObject(int i, String script) {
    try {
    String s = Parser.getNextQuotedString(script, i);
    if (s.indexOf("array") == 0) {
      String[] pts = TextFormat.split(s.substring(6, s.length() -1), ",");
      return new Point3f[] {
          (Point3f) Escape.unescapePoint(pts[0]), 
          (Point3f) Escape.unescapePoint(pts[1]), 
          (Point3f) Escape.unescapePoint(pts[2]), 
          (Point3f) Escape.unescapePoint(pts[3])};
    }
    return Escape.unescapePoint(s); // Point4f
    }
    catch (Exception e) {
      return null;
    }
  }

  private void initializeIsosurface() {
    lighting = JmolConstants.FRONTLIT;
    if (!iHaveModelIndex)
      modelIndex = viewer.getCurrentModelIndex();
    isFixed = (modelIndex < 0);
    if (modelIndex < 0)
      modelIndex = 0; // but note that modelIndex = -1
    // is critical for surfaceGenerator. Setting this equal to 
    // 0 indicates only surfaces for model 0.
    title = null;
    explicitContours = false;
    atomIndex = -1;
    colix = Graphics3D.ORANGE;
    defaultColix = meshColix = 0;
    isPhaseColored = isColorExplicit = false;
    allowContourLines = true; //but not for f(x,y) or plane, which use mesh
    center = new Point3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    offset = null;
    scale3d = 0;
    withinPoints = null;
    linkedMesh = null;
    initState();
  }

  private void initState() {
    associateNormals = true;
    sg.initState();
    //TODO   need to pass assocCutoff to sg
  }

  /*
   void checkFlags() {
   if (viewer.getTestFlag2())
   associateNormals = false;
   if (!logMessages)
   return;
   Logger.info("Isosurface using testflag2: no associative grouping = "
   + !associateNormals);
   Logger.info("IsosurfaceRenderer using testflag4: show vertex normals = "
   + viewer.getTestFlag4());
   Logger
   .info("For grid points, use: isosurface delete myiso gridpoints \"\"");
   }
   */

  private void discardTempData(boolean discardAll) {
    if (!discardAll)
      return;
    title = null;
    if (thisMesh == null)
      return;
    thisMesh.surfaceSet = null;
  }

  ////////////////////////////////////////////////////////////////
  // default color stuff (deprecated in 11.2)
  ////////////////////////////////////////////////////////////////

  private int indexColorPositive;
  private int indexColorNegative;

  private short getDefaultColix() {
    if (defaultColix != 0)
      return defaultColix;
    if (!sg.isCubeData())
      return colix; // orange
    int argb;
    if (sg.getCutoff() >= 0) {
      indexColorPositive = (indexColorPositive % JmolConstants.argbsIsosurfacePositive.length);
      argb = JmolConstants.argbsIsosurfacePositive[indexColorPositive++];
    } else {
      indexColorNegative = (indexColorNegative % JmolConstants.argbsIsosurfaceNegative.length);
      argb = JmolConstants.argbsIsosurfaceNegative[indexColorNegative++];
    }
    return Graphics3D.getColix(argb);
  }

  ///////////////////////////////////////////////////
  ////  LCAO Cartoons  are sets of lobes ////

  private int nLCAO = 0;

  private void drawLcaoCartoon(Vector3f z, Vector3f x, Vector3f rotAxis, int nElectrons) {
    String lcaoCartoon = sg.setLcao();
    //really rotRadians is just one of these -- x, y, or z -- not all
    float rotRadians = rotAxis.x + rotAxis.y + rotAxis.z;
    defaultColix = Graphics3D.getColix(sg.getColor(1));
    int colorNeg = sg.getColor(-1);
    Vector3f y = new Vector3f();
    boolean isReverse = (lcaoCartoon.length() > 0 && lcaoCartoon.charAt(0) == '-');
    if (isReverse)
      lcaoCartoon = lcaoCartoon.substring(1);
    int sense = (isReverse ? -1 : 1);
    y.cross(z, x);
    if (rotRadians != 0) {
      AxisAngle4f a = new AxisAngle4f();
      if (rotAxis.x != 0)
        a.set(x, rotRadians);
      else if (rotAxis.y != 0)
        a.set(y, rotRadians);
      else
        a.set(z, rotRadians);
      Matrix3f m = new Matrix3f();
      m.set(a);
      m.transform(x);
      m.transform(y);
      m.transform(z);
    }
    if (thisMesh == null && nLCAO == 0)
      nLCAO = meshCount;
    String id = (thisMesh == null ? (nElectrons > 0 ? "lp" : "lcao") + (++nLCAO) + "_" + lcaoCartoon
        : thisMesh.thisID);
    if (thisMesh == null)
      allocMesh(id, null);
    if (lcaoCartoon.equals("px")) {
      thisMesh.thisID += "a";
      Mesh meshA = thisMesh;
      createLcaoLobe(x, sense, nElectrons);
      if (nElectrons > 0) 
        return;
      setProperty("thisID", id + "b", null);
      createLcaoLobe(x, -sense, nElectrons);
      thisMesh.colix = Graphics3D.getColix(colorNeg);
      linkedMesh = thisMesh.linkedMesh = meshA;
      return;
    }
    if (lcaoCartoon.equals("py")) {
      thisMesh.thisID += "a";
      Mesh meshA = thisMesh;
      createLcaoLobe(y, sense, nElectrons);
      if (nElectrons > 0) 
        return;
      setProperty("thisID", id + "b", null);
      createLcaoLobe(y, -sense, nElectrons);
      thisMesh.colix = Graphics3D.getColix(colorNeg);
      linkedMesh = thisMesh.linkedMesh = meshA;
      return;
    }
    if (lcaoCartoon.equals("pz")) {
      thisMesh.thisID += "a";
      Mesh meshA = thisMesh;
      createLcaoLobe(z, sense, nElectrons);
      if (nElectrons > 0) 
        return;
      setProperty("thisID", id + "b", null);
      createLcaoLobe(z, -sense, nElectrons);
      thisMesh.colix = Graphics3D.getColix(colorNeg);
      linkedMesh = thisMesh.linkedMesh = meshA;
      return;
    }
    if (lcaoCartoon.equals("pza") 
        || lcaoCartoon.indexOf("sp") == 0 
        || lcaoCartoon.indexOf("d") == 0 
        || lcaoCartoon.indexOf("lp") == 0) {
      createLcaoLobe(z, sense, nElectrons);
      return;
    }
    if (lcaoCartoon.equals("pzb")) {
      createLcaoLobe(z, -sense, nElectrons);
      return;
    }
    if (lcaoCartoon.equals("pxa")) {
      createLcaoLobe(x, sense, nElectrons);
      return;
    }
    if (lcaoCartoon.equals("pxb")) {
      createLcaoLobe(x, -sense, nElectrons);
      return;
    }
    if (lcaoCartoon.equals("pya")) {
      createLcaoLobe(y, sense, nElectrons);
      return;
    }
    if (lcaoCartoon.equals("pyb")) {
      createLcaoLobe(y, -sense, nElectrons);
      return;
    }
    if (lcaoCartoon.equals("spacefill") || lcaoCartoon.equals("cpk")) {
      createLcaoLobe(null, 2 * viewer.getAtomRadius(atomIndex), nElectrons);
      return;      
    }

    // assume s
    createLcaoLobe(null, 1, nElectrons);
    return;
  }

  private Point4f lcaoDir = new Point4f();

  private void createLcaoLobe(Vector3f lobeAxis, float factor, int nElectrons) {
    initState();
    if (Logger.debugging) {
      Logger.debug("creating isosurface ID " + thisMesh.thisID);
    }
    thisMesh.colix = defaultColix;
    if (lobeAxis == null) {
      setProperty("sphere", new Float(factor / 2f), null);
    } else {
      lcaoDir.x = lobeAxis.x * factor;
      lcaoDir.y = lobeAxis.y * factor;
      lcaoDir.z = lobeAxis.z * factor;
      lcaoDir.w = 0.7f;
      setProperty(nElectrons == 2 ? "lp" : nElectrons == 1 ? "rad" : "lobe", 
          lcaoDir, null);
    }
    setScriptInfo(null);
  }

  /////////////// meshDataServer interface /////////////////

  public void invalidateTriangles() {
    thisMesh.invalidatePolygons();
  }

  public void fillMeshData(MeshData meshData, int mode, IsosurfaceMesh mesh) {
    if (meshData == null) {
      if (thisMesh == null)
        allocMesh(null, null);
      thisMesh.clear("isosurface", sg.getIAddGridPoints());
      thisMesh.colix = getDefaultColix();
      thisMesh.meshColix = meshColix;
      if (isPhaseColored || thisMesh.jvxlData.isBicolorMap)
        thisMesh.isColorSolid = false;
      return;
    }
    if (mesh == null)
      mesh = thisMesh;
    if (mesh == null)
      return;
    switch (mode) {
    case MeshData.MODE_GET_VERTICES:
      meshData.vertices = mesh.vertices;
      meshData.vertexValues = mesh.vertexValues;
      meshData.vertexCount = mesh.vertexCount;
      meshData.vertexIncrement = mesh.vertexIncrement;
      meshData.polygonCount = mesh.polygonCount;
      meshData.polygonIndexes = mesh.polygonIndexes;
      meshData.polygonColixes = mesh.polygonColixes;
      return;
    case MeshData.MODE_GET_COLOR_INDEXES:
      if (mesh.vertexColixes == null
          || mesh.vertexCount > mesh.vertexColixes.length)
        mesh.vertexColixes = new short[mesh.vertexCount];
      meshData.vertexColixes = mesh.vertexColixes;
      meshData.polygonIndexes = null;
      return;
    case MeshData.MODE_PUT_SETS:
      mesh.surfaceSet = meshData.surfaceSet;
      mesh.vertexSets = meshData.vertexSets;
      mesh.nSets = meshData.nSets;
      return;
    case MeshData.MODE_PUT_VERTICES:
      mesh.vertices = meshData.vertices;
      mesh.vertexValues = meshData.vertexValues;
      mesh.vertexCount = meshData.vertexCount;
      mesh.vertexIncrement = meshData.vertexIncrement;
      mesh.polygonCount = meshData.polygonCount;
      mesh.polygonIndexes = meshData.polygonIndexes;
      mesh.polygonColixes = meshData.polygonColixes;
      return;
    }
  }

  public void notifySurfaceGenerationCompleted() {
    setModelIndex();
    thisMesh.insideOut = sg.isInsideOut();
    thisMesh.calculatedArea = null;
    thisMesh.calculatedVolume = null;
    thisMesh.initialize(sg.getPlane() != null ? JmolConstants.FULLYLIT
        : lighting, null, sg.getPlane());
    if (thisMesh.jvxlData.jvxlPlane != null)
      allowContourLines = false;
    thisMesh.isSolvent = ((sg.getDataType() & Parameters.IS_SOLVENTTYPE) != 0);
  }

  public void notifySurfaceMappingCompleted() {
    setModelIndex();
    String schemeName = colorEncoder.getColorSchemeName();
    boolean isTranslucentScheme = colorEncoder.isTranslucent();
    //viewer.setPropertyColorScheme(schemeName, sg.getParams().colorSchemeTranslucent, false);
    //viewer.setCurrentColorRange(jvxlData.valueMappedToRed,
      //  jvxlData.valueMappedToBlue);
    thisMesh.isColorSolid = false;
    thisMesh.colorDensity = jvxlData.colorDensity;
    thisMesh.getContours();
    if (thisMesh.jvxlData.jvxlPlane != null)
      allowContourLines = false;
    if (thisMesh.jvxlData.nContours != 0 && thisMesh.jvxlData.nContours != -1)
      explicitContours = true;
    if (explicitContours && thisMesh.jvxlData.jvxlPlane != null)
      thisMesh.havePlanarContours = true;
    setPropertySuper("token", new Integer(explicitContours ? Token.nofill : Token.fill), null);
    setPropertySuper("token", new Integer(explicitContours ? Token.contourlines : Token.nocontourlines), null);
    // may not be the final color scheme, though.
    setColorCommand(schemeName, isTranslucentScheme);
    /*
     viewer.setCurrentColorRange(jvxlData.mappedDataMin, jvxlData.mappedDataMax);
     thisMesh.isColorSolid = false;
     thisMesh.colorCommand = "color $" + thisMesh.thisID + " " + getUserColorScheme(schemeName) + " range " 
     + (jvxlData.isColorReversed ? jvxlData.mappedDataMax + " " + jvxlData.mappedDataMin : 
     jvxlData.mappedDataMin + " " + jvxlData.mappedDataMax);

     */
  }

  private void setColorCommand(String schemeName, boolean isTranslucent) {
    if (thisMesh == null)
      return;
    String colors = viewer.getColorSchemeList(schemeName, false);
    thisMesh.colorCommand = "color $" + thisMesh.thisID
        + (isTranslucent ? " translucent " : " ")
        + Escape.escape(colors.length() == 0 ? schemeName : colors) + " range ";
    thisMesh.colorCommand += (jvxlData.isColorReversed ? jvxlData.valueMappedToBlue
        + " " + jvxlData.valueMappedToRed
        : jvxlData.valueMappedToRed + " " + jvxlData.valueMappedToBlue);
  }

  public Point3f[] calculateGeodesicSurface(BitSet bsSelected,
                                            float envelopeRadius) {
    return viewer.calculateSurface(bsSelected, envelopeRadius);
  }

  /////////////  VertexDataServer interface methods ////////////////

  public int getSurfacePointIndexAndFraction(float cutoff, boolean isCutoffAbsolute,
                                  int x, int y, int z, Point3i offset, int vA,
                                  int vB, float valueA, float valueB,
                                  Point3f pointA, Vector3f edgeVector,
                                  boolean isContourType, float[] fReturn) {
    return 0;
  }

  private boolean associateNormals;

  public int addVertexCopy(Point3f vertexXYZ, float value, int assocVertex) {
    return (withinPoints != null && !checkWithin(vertexXYZ) ? -1
        : thisMesh.addVertexCopy(vertexXYZ, value, assocVertex,
        associateNormals));
  }

  private boolean checkWithin(Point3f pti) {
    for (int i = withinPoints.size(); --i >= 0; )
      if (pti.distance((Point3f) withinPoints.get(i)) <= withinDistance) {
        return true; 
      }
    return false;
  }

  public int addTriangleCheck(int iA, int iB, int iC, int check,
                              int check2, boolean isAbsolute, int color) {
   return (iA < 0 || iB < 0 || iC < 0 
       || isAbsolute && !MeshData.checkCutoff(iA, iB, iC, thisMesh.vertexValues)
       ? -1 : thisMesh.addTriangleCheck(iA, iB, iC, check, check2, color));
  }

  private void setModelIndex() {
    setModelIndex(atomIndex, modelIndex);
    thisMesh.ptCenter.set(center);
    thisMesh.ptOffset = offset;
    thisMesh.scale3d = (thisMesh.jvxlData.jvxlPlane == null ? 0 : scale3d);
  }

  protected void setScriptInfo(String strCommand) {
    // also from lcaoCartoon
    thisMesh.title = sg.getTitle();
    String script = (strCommand == null ? sg.getScript() : strCommand);
    thisMesh.dataType = sg.getParams().dataType;
    thisMesh.scale3d = sg.getParams().scale3d;
    thisMesh.bitsets = null;
    thisMesh.slabbingObject = sg.getParams().slabbingObject;
    thisMesh.cappingObject = sg.getParams().cappingObject;
    if (script != null) {
      if (script.charAt(0) == ' ') {
        script = myType + " ID " + Escape.escape(thisMesh.thisID) + script;
      } else if (sg.getIUseBitSets()) {
        thisMesh.bitsets = new BitSet[3];
        thisMesh.bitsets[0] = sg.getBsSelected();
        thisMesh.bitsets[1] = sg.getBsIgnore();
        thisMesh.bitsets[2] = viewer.getBitSetTrajectories();
      }
    }
    int pt;
    if (!explicitID && script != null && (pt = script.indexOf("# ID=")) >= 0)
      thisMesh.thisID = Parser.getNextQuotedString(script, pt);
    thisMesh.scriptCommand = script;
//    Vector v = (Vector) sg.getFunctionXYinfo();
//    if (thisMesh.data1 == null)
//      thisMesh.data1 = v;
//    else
 //     thisMesh.data2 = v;
  }

  private void setJvxlInfo() {
    if (sg.getJvxlData() != jvxlData || sg.getJvxlData() != thisMesh.jvxlData)
      jvxlData = thisMesh.jvxlData = sg.getJvxlData();
  }

  public Vector getShapeDetail() {
    Vector V = new Vector();
    for (int i = 0; i < meshCount; i++) {
      Hashtable info = new Hashtable();
      IsosurfaceMesh mesh = isomeshes[i];
      if (mesh == null || mesh.vertices == null)
        continue;
      info.put("ID", (mesh.thisID == null ? "<noid>" : mesh.thisID));
      info.put("vertexCount", new Integer(mesh.vertexCount));
      if (mesh.ptCenter.x != Float.MAX_VALUE)
        info.put("center", mesh.ptCenter);
      info.put("offset", (mesh.ptOffset == null ? new Point3f() : mesh.ptOffset));
      if (mesh.scale3d != 0)
        info.put("scale3d", new Float(mesh.scale3d));
      info.put("xyzMin", mesh.jvxlData.boundingBox[0]);
      info.put("xyzMax", mesh.jvxlData.boundingBox[1]);
      String s = JvxlCoder.jvxlGetInfo(mesh.jvxlData);
      if (s != null)
        info.put("jvxlInfo", s.replace('\n', ' '));
      info.put("modelIndex", new Integer(mesh.modelIndex));
      if (mesh.title != null)
        info.put("title", mesh.title);
      if (mesh.jvxlData.contourValues != null || mesh.jvxlData.contourValuesUsed != null)
        info.put("contours", mesh.getContourList(viewer));
      V.addElement(info);
    }
    return V;
  }

  private void remapColors(String scheme, boolean isTranslucentScheme, float[] range) {
    if (scheme == null) {
      scheme = viewer.getPropertyColorScheme();
      if (scheme.startsWith("translucent ")) {
        isTranslucentScheme = true;
        scheme = scheme.substring(12).trim();
      }
    }
    JvxlData jvxlData = thisMesh.jvxlData;
    float[] vertexValues = thisMesh.vertexValues;
    short[] vertexColixes = thisMesh.vertexColixes;
    thisMesh.polygonColixes = null;
    if (vertexValues == null || jvxlData.isBicolorMap
        || jvxlData.vertexCount == 0)
      return;
    if (vertexColixes == null)
      vertexColixes = thisMesh.vertexColixes = new short[thisMesh.vertexCount];
    thisMesh.jvxlData.isColorReversed = (range[0] > range[1]); 
    if (range[1] != Float.MAX_VALUE) {
      jvxlData.valueMappedToRed = Math.min(range[0], range[1]);
      jvxlData.valueMappedToBlue = Math.max(range[0], range[1]);
    }
    ColorEncoder ce = new ColorEncoder();
    ce.setColorScheme(scheme, isTranslucentScheme);
    ce.setRange(jvxlData.valueMappedToRed, jvxlData.valueMappedToBlue, thisMesh.jvxlData.isColorReversed);
    // thisMesh.colix must be translucent if the scheme is translucent
    // but may be translucent if the scheme is not translucent
    boolean isTranslucent = Graphics3D.isColixTranslucent(thisMesh.colix);
    if (isTranslucentScheme) {
      if (!isTranslucent)
        thisMesh.colix = Graphics3D.getColixTranslucent(thisMesh.colix, isTranslucentScheme, 0.5f);
      // still, if the scheme is translucent, we don't want to color the vertices translucent
      isTranslucent = false;
    }
    for (int i = thisMesh.vertexCount; --i >= 0;) {
      vertexColixes[i] = ce.getColorIndex(vertexValues[i]);
      if (isTranslucent)
        vertexColixes[i] = Graphics3D.getColixTranslucent(vertexColixes[i], true, translucentLevel);
    }
    Vector[] contours = thisMesh.getContours();
    if (contours != null) {
      for (int i = contours.length; --i >= 0; ) {
        float value = ((Float)contours[i].get(JvxlCoder.CONTOUR_VALUE)).floatValue();
        short[] colix = ((short[])contours[i].get(JvxlCoder.CONTOUR_COLIX));
        colix[0] = ce.getColorIndex(value);
        int[] color = ((int[])contours[i].get(JvxlCoder.CONTOUR_COLOR));
        color[0] = Graphics3D.getArgb(colix[0]);
      }
    }
    //TODO -- still not right.
    if (thisMesh.contourValues != null) {
      thisMesh.contourColixes = new short[thisMesh.contourValues.length];
      for (int i = 0; i < thisMesh.contourValues.length; i++) 
        thisMesh.contourColixes[i] = ce.getColorIndex(thisMesh.contourValues[i]);
      thisMesh.setDiscreteColixes(null, null);
    }
    jvxlData.isJvxlPrecisionColor = true;
    JvxlCoder.jvxlCreateColorData(jvxlData, vertexValues);
    setColorCommand(scheme, isTranslucentScheme);
    thisMesh.isColorSolid = false;
  }

  public void getPlane(int x) {
    // only for surface readers
  }
  
  public float getValue(int x, int y, int z, int ptyz) {
    return 0;
  }
  
  public boolean checkObjectHovered(int x, int y, BitSet bsVisible) {
    if (!viewer.getDrawHover())
      return false;
    String s = findValue(x, y, false, bsVisible);
    if (s == null)
      return false;
    if (g3d.isDisplayAntialiased()) {
      //because hover rendering is done in FIRST pass only
      x <<= 1;
      y <<= 1;
    }      
    viewer.hoverOn(x, y, s);
    return true;
  }

  private final static int MAX_OBJECT_CLICK_DISTANCE_SQUARED = 10 * 10;
  private final Point3i ptXY = new Point3i();

  public Point3fi checkObjectClicked(int x, int y, int action, BitSet bsVisible) {
    if (!viewer.isBound(action, ActionManager.ACTION_pickIsosurface))
      return null;
    int dmin2 = MAX_OBJECT_CLICK_DISTANCE_SQUARED;
    if (g3d.isAntialiased()) {
      x <<= 1;
      y <<= 1;
      dmin2 <<= 1;
    }
    int imesh = -1;
    int jmaxz = -1;
    int jminz = -1;
    int maxz = Integer.MIN_VALUE;
    int minz = Integer.MAX_VALUE;
    boolean pickFront = viewer.getDrawPicking();
    for (int i = 0; i < meshCount; i++) {
      IsosurfaceMesh m = isomeshes[i];
      if (!isPickable(m, bsVisible))
        continue;
      Point3f[] centers = m.getCenters();
      for (int j = centers.length; --j >= 0; ) {
          Point3f v = centers[j];
          if (v == null)
            continue;
          int d2 = coordinateInRange(x, y, v, dmin2, ptXY);
          if (d2 >= 0) {
            if (ptXY.z < minz) {
              if (pickFront)
                imesh = i;
              minz = ptXY.z;
              jminz = j;
            }
            if (ptXY.z > maxz) {
              if (!pickFront)
                imesh = i;
              maxz = ptXY.z;
              jmaxz = j;
            }
          }
      }
    }
    if (imesh < 0)
      return null;
    pickedMesh = isomeshes[imesh];
    setPropertySuper("thisID", pickedMesh.thisID, null);
    int iFace = pickedVertex = (pickFront ? jminz : jmaxz);
    Point3fi ptRet = new Point3fi();
    ptRet.set(((IsosurfaceMesh)pickedMesh).centers[iFace]);
    pickedModel = ptRet.modelIndex = (short) pickedMesh.modelIndex;
    ptRet.index = imesh;
    if (pickFront) {
      setStatusPicked(-4, ptRet);
    } else {
      Vector3f vNorm = new Vector3f();
      ((IsosurfaceMesh)pickedMesh).getFacePlane(iFace, vNorm);
      // get normal to surface
      vNorm.scale(-1);
      setHeading(ptRet, vNorm, 2);
    }
    return ptRet;
  }

  private boolean isPickable(IsosurfaceMesh m, BitSet bsVisible) {
    return (m.visibilityFlags == 0 || m.modelIndex >= 0
        && !bsVisible.get(m.modelIndex) || !Graphics3D
        .isColixTranslucent(m.colix));
  }

  private void navigate(int dz) {
    if (thisMesh == null)
      return;
    Point3f navPt = new Point3f(viewer.getNavigationOffset());
    Point3f toPt = new Point3f();
    viewer.unTransformPoint(navPt, toPt);
    navPt.z += dz;
    viewer.unTransformPoint(navPt, toPt);
    Point3f ptRet = new Point3f();
    Vector3f vNorm = new Vector3f();
    if (!getClosestNormal(thisMesh, toPt, ptRet, vNorm))
      return;
    Point3f pt2 = new Point3f(ptRet);
    pt2.add(vNorm);
    Point3f pt2s = new Point3f();
    viewer.transformPoint(pt2, pt2s);
    if (pt2s.y > navPt.y)
      vNorm.scale(-1);
    setHeading(ptRet, vNorm, 0);     
  }

  private void setHeading(Point3f pt, Vector3f vNorm, int nSeconds) {
    // general trick here is to save the original orientation, 
    // then do all the changes and save the new orientation.
    // Then just do a timed restore.

    Orientation o1 = viewer.getOrientation();
    
    // move to point
    viewer.navigate(0, pt);
    
    Point3f toPts = new Point3f();
    
    // get screen point along normal
    Point3f toPt = new Point3f(vNorm);
    //viewer.script("draw test2 vector " + Escape.escape(pt) + " " + Escape.escape(toPt));
    toPt.add(pt);
    viewer.transformPoint(toPt, toPts);
    
    // subtract the navigation point to get a relative point
    // that we can project into the xy plane by setting z = 0
    Point3f navPt = new Point3f(viewer.getNavigationOffset());
    toPts.sub(navPt);
    toPts.z = 0;
    
    // set the directed angle and rotate normal into yz plane,
    // less 20 degrees for the normal upward sloping view
    float angle = Measure.computeTorsion(JmolConstants.axisNY, 
        JmolConstants.center, JmolConstants.axisZ, toPts, true);
    viewer.navigate(0, JmolConstants.axisZ, angle);        
    toPt.set(vNorm);
    toPt.add(pt);
    viewer.transformPoint(toPt, toPts);
    toPts.sub(navPt);
    angle = Measure.computeTorsion(JmolConstants.axisNY,
        JmolConstants.center, JmolConstants.axisX, toPts, true);
    viewer.navigate(0, JmolConstants.axisX, 20 - angle);
    
    // save this orientation, restore the first, and then
    // use TransformManager.moveto to smoothly transition to it
    // a script is necessary here because otherwise the application
    // would hang.
    
    navPt = new Point3f(viewer.getNavigationOffset());
    if (nSeconds <= 0)
      return;
    viewer.saveOrientation("_navsurf");
    o1.restore(0, true);
    viewer.script("restore orientation _navsurf " + nSeconds);
  }
  
  private boolean getClosestNormal(IsosurfaceMesh m, Point3f toPt, Point3f ptRet, Vector3f normalRet) {
    Point3f[] centers = m.getCenters();
    float d;
    float dmin = Float.MAX_VALUE;
    int imin = -1;
    for (int i = centers.length; --i >= 0; ) {
      if ((d = centers[i].distance(toPt)) >= dmin)
        continue;
      dmin = d;
      imin = i;
    }
    if (imin < 0)
      return false;
    getClosestPoint(m, imin, toPt, ptRet, normalRet);
    return true;
  }
  
  private void getClosestPoint(IsosurfaceMesh m, int imin, Point3f toPt, Point3f ptRet,
                               Vector3f normalRet) {
    Point4f plane = m.getFacePlane(imin, normalRet);
    float dist = Measure.distanceToPlane(plane, toPt);
    normalRet.scale(-dist);
    ptRet.set(toPt);
    ptRet.add(normalRet);
    dist = Measure.distanceToPlane(plane, ptRet);
    if (m.centers[imin].distance(toPt) < ptRet.distance(toPt))
      ptRet.set(m.centers[imin]);
  }

  private String findValue(int x, int y, boolean isPicking, BitSet bsVisible) {
    int dmin2 = MAX_OBJECT_CLICK_DISTANCE_SQUARED;
    if (g3d.isAntialiased()) {
      x <<= 1;
      y <<= 1;
      dmin2 <<= 1;
    }
    Vector pickedContour = null;
    int pickedVertex = -1;
    for (int i = 0; i < meshCount; i++) {
      IsosurfaceMesh m = isomeshes[i];
      if (!isPickable(m, bsVisible))
        continue;
      Vector[] vs = m.jvxlData.vContours;
      int ilast = (m.firstRealVertex < 0 ? 0 : m.firstRealVertex);
      if (vs != null && vs.length > 0) {
        for (int j = 0; j < vs.length; j++) {
          Vector vc = vs[j];
          int n = vc.size() - 1;
          for (int k = JvxlCoder.CONTOUR_POINTS; k < n; k++) {
            Point3f v = (Point3f) vc.get(k);
            int d2 = coordinateInRange(x, y, v, dmin2, ptXY);
            if (d2 >= 0) {
              dmin2 = d2;
              pickedContour = vc;
            }
          }
        }
        if (pickedContour != null)
          return pickedContour.get(JvxlCoder.CONTOUR_VALUE).toString();
      } else if (m.jvxlData.jvxlPlane != null && m.vertexValues != null) {
        Point3f[] vertices = (m.ptOffset == null && m.scale3d == 0 
            ? m.vertices : m.getOffsetVertices(m.jvxlData.jvxlPlane)); 
        for (int k = m.vertexCount; --k >= ilast;) {
          Point3f v = vertices[k];
          int d2 = coordinateInRange(x, y, v, dmin2, ptXY);
          if (d2 >= 0) {
            dmin2 = d2;
            pickedVertex = k;
          }
        }
      } else if (m.vertexValues != null) {
        for (int k = m.vertexCount; --k >= m.firstRealVertex;) {
          Point3f v = m.vertices[k];
          int d2 = coordinateInRange(x, y, v, dmin2, ptXY);
          if (d2 >= 0) {
            dmin2 = d2;
            pickedVertex = k;
          }
        }
      }      
      if (pickedVertex != -1)
        return (Logger.debugging ? "v" + pickedVertex + " "  + m.vertices[pickedVertex] + ": " : "") + m.vertexValues[pickedVertex];
    }
    return null;
  }

  public void merge(Shape shape) {
    super.merge(shape);
  }
}
