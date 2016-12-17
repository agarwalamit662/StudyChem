<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="StudyChem | Short description about StudyChem">
    <meta name="author" content="Your name">
    <title>StudyChem</title>
    <!-- Bootstrap -->
    <link href="<?php echo base_url('assests/css/bootstrap.min.css'); ?>" rel="stylesheet">
    <!-- Bootstrap responsive -->
    <link href="<?php echo base_url('assests/css/bootstrap-responsive.min.css'); ?>" rel="stylesheet">
    <!-- Font awesome - iconic font with IE7 support --> 
    <link href="<?php echo base_url('assests/css/font-awesome.css'); ?>" rel="stylesheet">
    <link href="<?php echo base_url('assests/css/font-awesome-ie7.css'); ?> "rel="stylesheet">
    <!-- Bootbusiness theme -->
    <link href="<?php echo base_url('assests/css/boot-business.css');?>" rel="stylesheet">
	 <script type="text/javascript" src="<?php echo base_url('assests/js/jquery.min.js'); ?>"></script>
    <script type="text/javascript" src="<?php echo base_url('assests/js/bootstrap.min.js'); ?>"></script>
    <script type="text/javascript" src="<?php echo base_url('assests/js/boot-business.js'); ?>"></script>
   <script type="text/javascript">	
		$( '#searchbychar' ).on('click', function(event) {
		//event.preventDefault();
		
		var target = "#" + $(this).data('target');
		console.log(target);
		$('html, body').animate({
        scrollDown: $(target).offset().down
		}, 500);
		});
		</script>
  
  </head>
  <body>
  
<div class="content" >
	
    <div class="container"> 
        <div class="page-header">
          <center>  <h2>Assignments</h2> </center>
        
		
		
        <div class="page-header">
        <h2> Assignments </h2>
        </div>
        <?php if($data -> num_rows() == 0) { ?>
        <div class="alert alert-info"> 
        <p> You have not uploaded any assignments for your class! </p>
        </div>
        <?php } else {?>
        <table class="table table-striped table-hover">
        <thead>
                <tr>
                  <th>S.No</th>
                  <th>Assignment</th>
                  <th>Details</th>
                  <th>Start Date</th>
				  <th>End Date</th>
                </tr>
        </thead>
        <tbody>
    
		<?php
        $i=1;
        foreach($data -> result() as $row) { 
		
      		$assgname = $row -> assgname;
            $this -> db -> where('assgname',$assgname);
            $r = $this -> db -> get('upload');
            $stu = $r->row();
            $assgname = $stu -> assgname;
            $assmsg = $stu -> assmsg;
			$startdate = $stu -> startdate;
            $lastdate = $stu -> lastdate;
            ?>
            
            <tr class="info">
                <td><?php echo $i; $i++;?> </td>
                <td><a href="<?php echo site_url('student/download/'.$assgname.'/');?>" >  <?php echo $assgname;?> </a> </td>
                <td> <?php echo $assmsg;?> </td>
                <td> <?php echo $startdate;?> </td>
				<td> <?php echo $lastdate;?> </td>
				<td> <?php echo $today = date("Y-m-d");?> </td>
				<td>
				 
				<?php if(strtotime($today) < strtotime($lastdate)){ ?>
				<form class="form-horizontal form-signin-signup" action="<?php echo base_url();?>student/do_upload/<?php echo $assgname;?>" method="post" enctype="multipart/form-data">
				<input type="file" name="userfile" size="20"  id="userfile" accept = "image/*"/></td>
				<input type="hidden" name="MAX_FILE_SIZE" value="2000000">
				<td><button name = "upload" class="btn btn-primary btn-large "  	 	id = "upload" type="submit" value="upload">Submit</button></td>
				<input type="hidden" name="assgname1" value="<?php echo $assgname; ?>"/>
				<input type="hidden" name="subdate1" value="<?php echo $today = date("Y-m-d");?> "/>
				<?php
				}
				else {
				?>
				<form class="form-horizontal form-signin-signup" action="<?php echo base_url();?>student/do_upload/<?php echo $assgname;?>" method="post" enctype="multipart/form-data">
				<input type="file" name="userfile" disabled="disabled" size="20"  id="userfile" accept = "image/*"/></td>
				<input type="hidden" name="MAX_FILE_SIZE" value="2000000">
				<td><button name = "upload" class="btn btn-primary btn-large " disabled="disabled"id = "upload" type="submit" value="upload">Submit</button></td>
				<input type="hidden" name="assgname1" value="<?php echo $assgname; ?>"/>
				<input type="hidden" name="subdate1" value="<?php echo $today = date("Y-m-d");?> "/>
				
				<?php } ?>
				</form>
				 
            </tr>
            
        <?php } ?>
        </tbody>
        </table>
        <?php } ?>

	<?php if(isset($title)) { ?>
        <div class="alert alert-success"> 
        <p> <?php echo $title?> </p>
        </div>
		<?php } ?> 
	

<div class="page-header">
        <h2> Submitted Assignments </h2>
        </div>
        <?php if($data2 -> num_rows() == 0) { ?>
        <div class="alert alert-info"> 
        <p> You have not uploaded any assignments </p>
        </div>
        <?php } else {?>
        <table class="table table-striped table-hover">
        <thead>
                <tr>
                  <th>S.No</th>
					<th>Submitted File</th>
				  <th>Submitted Date</th>
                </tr>
        </thead>
        <tbody>
    
		<?php
        $i=1;
        foreach($data2 -> result() as $row) { 
		
      		$assgname = $row -> assgname;
            $this -> db -> where('assgname',$assgname);
            $r = $this -> db -> get('submission');
            $stu = $r->row();
            $assgname = $stu -> assgname;
           
			$subdate = $stu -> subdate;
           
            ?>
            
            <tr class="info">
                <td><?php echo $i; $i++;?> </td>
                <td><a href="<?php echo site_url('student/download1/'.$assgname.'/');?>" >  <?php echo $assgname;?> </a> </td>
                <td> <?php echo $subdate;?> </td>
				
				 
            </tr>
            
        <?php } ?>
        </tbody>
        </table>
        <?php } ?>

   
</div>

</div>
<div class="content" style ="height: -20px;">
	 </div>
	 




<script>
function search() {
 
   var name = document.getElementById("searchForm").elements["searchItem"].value;
   var pattern = name.toLowerCase();
   var targetId = "";
 
   var divs = document.getElementsByClassName("col-md-3");
   for (var i = 0; i < divs.length; i++) {
      var para = divs[i].getElementsByTagName("p");
      var index = para[0].innerText.toLowerCase().indexOf(pattern);
      if (index != -1) {
         targetId = divs[i].parentNode.id;
         document.getElementById(targetId).scrollIntoView();
         break;
      }
   }  
}
</script>
       
       </body>
	   </html>