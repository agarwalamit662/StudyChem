<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/* This controller contains the functionalities 
/* of the student account.
/*********************************************/

class Student extends CI_Controller {
	function __construct()
   	{
        // this is your constructor
        parent::__construct();
        $this->load->helper('form');
        $this->load->helper('url');
        $this->load->library('session');
    }
	
	public function assignments() 
	{
		
		$error = null;
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
		$this->output->set_header("Pragma: no-cache");
		$email = $this -> session -> userdata('email');
		$this -> db -> where('student',$email);
		$data1 = $this -> db -> get('teacherClass');
		
		$teacher = $data1 -> row();
		$emailtea = $teacher -> teacher;
		$this -> db -> where('teacher',$emailtea);
		$data = $this -> db -> get('addassignment');
		$sendData['data'] = $data;
		
		$this -> db -> where('teacher',$emailtea);
		$this -> db -> where('student',$email);
		$data = $this -> db -> get('submission');
		$sendData['data2'] = $data;
		$this -> load -> view('assignments',$sendData);
		
		$this -> load -> view('header');
		//$this -> load -> view('assignments',$sendData);
	}
	
	public function download($assgname)
	{
		$this -> output -> set_header('Last-Modified:' . gmdate('D, d M Y H:i:s') . 'GMT');
		$this -> output -> set_header('Cache-Control: no-store, no-cache, must-revalidate');
		$this -> output -> set_header('Cache-Control: post-check=0, pre-check=0', false);
		$this -> output -> set_header('Pragma: no-cache');
		
		if ($this -> session -> userdata('isLoggedIn') && $this -> session -> userdata('roleid') == 1) {
			$this -> load -> model('studentmodel');
			$this -> db -> where('assgname',$assgname);
			$data = $this -> db -> get('upload');
			$data = $data -> row();
			$filePath = $data -> path;
			
			$getFile = explode("/", $filePath);
			$name = end($getFile);
			$this -> load -> helper('download');
			$data = file_get_contents($filePath);
			force_download($name, $data);		
		} 
		
		else {
			$this -> session -> sess_destroy();
			redirect(base_url() . "Home/Login");
		}
	
	}
	public function download1($assgname)
	{
		$this -> output -> set_header('Last-Modified:' . gmdate('D, d M Y H:i:s') . 'GMT');
		$this -> output -> set_header('Cache-Control: no-store, no-cache, must-revalidate');
		$this -> output -> set_header('Cache-Control: post-check=0, pre-check=0', false);
		$this -> output -> set_header('Pragma: no-cache');
		$email = $this -> session -> userdata('email');
		if ($this -> session -> userdata('isLoggedIn') && $this -> session -> userdata('roleid') == 1) {
			$this -> load -> model('studentmodel');
			$this -> db -> where('assgname',$assgname);
			$this->db ->where('student' , $email);
			$data = $this -> db -> get('submission');
			$data = $data -> row();
			$filePath = $data -> path;
			
			$getFile = explode("/", $filePath);
			$name = end($getFile);
			$this -> load -> helper('download');
			$data = file_get_contents($filePath);
			force_download($name, $data);		
		} 
		
		else {
			$this -> session -> sess_destroy();
			redirect(base_url() . "Home/Login");
		}
	
	}
	public function index()
	{
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
$this->output->set_header("Pragma: no-cache");
		$this -> load -> model('studentmodel');
		$this -> load -> view('header');
		$this->load->view('home');
		
	}
	
	public function quiz($req,$qid) 
	{
		$error = null;
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
		$this->output->set_header("Pragma: no-cache");
		$email = $this -> session -> userdata('email');
		$quizid = $qid;
		
		$this->db->where('quizid',$quizid);
		$data = $this -> db -> get('problem');

		

		$count = $data->num_rows();
		$this->db->where('quizid',$quizid);
		$this->db->where('ques_num',$req);
		$data = $this -> db -> get('problem');
		
		$sendData['data'] = $data;
		$sendData['count'] = $count;
		$sendData['quizid'] = $qid;
		
		$this->db->where('quizid',$quizid);
		$this->db->where('student',$email);
		$quiz = $this -> db -> get('timer');
		$sendData['quiz'] = $quiz;
		
		$this -> load -> view('header');
		$this -> load -> view('quiz',$sendData);
		
	}
	public function viewquiz()
	{
	$error = null;
	$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
	$this->output->set_header("Pragma: no-cache");
	$email = $this -> session -> userdata('email');
	$senddata['user'] = $email;
	$this->db->where('student',$email);
	$tc = $this -> db -> get('teacherclass');
	$data = $tc -> row();
	$teacher = $data -> teacher;
	
	$this->db->where('teacher',$teacher);
	$quiz = $this -> db -> get('quiz_table');
	$senddata['quiz'] = $quiz;
	
	$this->db->where('student',$email);
	$timer = $this -> db -> get('timer');
	$senddata['timer'] = $timer;
	$senddata['user'] = $email;
	
	$this -> load -> view('header');
	$this -> load -> view('viewquiz',$senddata);
	}
	
	public function submitquiz($qno,$qid,$option) 
	{
		$error = null;
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
		$this->output->set_header("Pragma: no-cache");
		$email = $this -> session -> userdata('email');
		$this -> db -> where('student',$email);
		$this -> db -> where('quizid',$qid);
		$this -> db -> where('quesid',$qno);
		$previous = $this -> db -> get('studentquiz');
		$row = $previous -> num_rows();
		if($row != 0){
		$this -> db -> where('student',$email);
		$this -> db -> where('quizid',$qid);
		$this -> db -> where('quesid',$qno);
		$data = array('student' => $email,
					  'quizid' => $qid,
					  'quesid' => $qno,
					  'marked' => $option
					 );
		$this -> db -> update('studentquiz',$data);
		}
		else {
		$data = array('student' => $email,
					  'quizid' => $qid,
					  'quesid' => $qno,
					  'marked' => $option
					 );
		$this -> db -> insert('studentquiz',$data);
		}
		
		$this -> markpaper($qid,$qno,$option);
	}
	
	public function endquiz($qid)
	{
		$error = null;
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
		$this->output->set_header("Pragma: no-cache");
		
		$email = $this -> session -> userdata('email');
		
		$this -> db -> where('student',$email);
		$this -> db -> where('quizid',$qid);
		$result = $this -> db -> get('results');
		$marks = $result->row() -> marks;
		$sendData['marks'] = $marks;
		$sendData['quizid'] = $qid;
	
		$this -> load -> view('header');
		
		$this -> load -> view('endquiz',$sendData);
		
	}
	public function settime($time,$qid)
	{
	
		$error = null;
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
		$this->output->set_header("Pragma: no-cache");
		$email = $this -> session -> userdata('email');
		
		$this -> db -> where('student',$email);
		$this -> db -> where('quizid',$qid);
		$data = array('student' => $email,
					  'quizid' => $qid,
					  'time_left' => $time
					 );
		$this -> db -> update('timer',$data);
		
		
		//Evalute
		$this->db->select('SUM(obtained) as total1');
		//$this->db->from('quiz_paper');
		$this->db->where('student',$email);
		$this->db->where('quizid',$qid);
		$result = $this -> db ->get('quiz_paper');
		
		//$query = "SELECT SUM(obtained) AS total FROM `quiz_paper` where student = "+$email +" AND quizid = "+$qid; 
		//$result = mysql_query($query) or die(mysql_error());
		if($qid != null){
		$marks = $result -> row()->total1;
		$data = array('student' => $email,
					  'quizid' => $qid,
					  'marks' => $marks
					 );
		$this -> db -> insert('results',$data);
		}
	
	}
	public function markpaper($qid,$qno,$option)
	{
		$error = null;
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
		$this->output->set_header("Pragma: no-cache");
		$email = $this -> session -> userdata('email');
		
		echo $qno;
		echo $qid;
		echo $option;
		$this -> db -> where('student',$email);
		$this -> db -> where('quizid',$qid);
		$this -> db -> where('quesid',$qno);
		$previous = $this -> db -> get('quiz_paper');
		$row = $previous -> num_rows();
		if($row != 0){
			$this -> db -> where('quizid',$qid);
			$this -> db -> where('ques_num',$qno);
			$row = $this -> db -> get('problem');
			$correct = $row -> row() -> correctans;
			if($correct === $option){
				$marks = $row -> row() -> marks;
				$wr = 1;
			}
			else{
				$marks = 0;
				$wr = 0;
			}
			$this -> db -> where('student',$email);
			$this -> db -> where('quizid',$qid);
			$this -> db -> where('quesid',$qno);
			$data = array('student' => $email,
						  'quizid' => $qid,
						  'quesid' => $qno,
						  'obtained' => $marks,
						  'wr' => $wr
						 );
			$this -> db -> update('quiz_paper',$data);
		}
		else {
			$this -> db -> where('quizid',$qid);
			$this -> db -> where('ques_num',$qno);
			$row = $this -> db -> get('problem');
			$correct = $row -> row() -> correctans;
			if($correct === $option){
				$marks = $row -> row() -> marks;
				$wr = 1;
			}
			else{
				$marks = 0;
				$wr = 0;
			}
			$data = array('student' => $email,
						  'quizid' => $qid,
						  'quesid' => $qno,
						  'obtained' => $marks,
						  'wr' => $wr
						 );
			$this -> db -> insert('quiz_paper',$data);
		}
		
	}
	
	
	public function settings() 
	{	
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
$this->output->set_header("Pragma: no-cache");
		$this -> load -> view('header');
		$this -> load -> view('settings');
	}	

	public function changePassword() {
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
$this->output->set_header("Pragma: no-cache");
		$Error["Invalid1"] = NULL;
		$this -> load -> helper('url');
		$this -> load -> library('session');
		$this -> load -> library("form_validation");
		$this -> load -> model('registermodel');
		$res = $this -> registermodel -> changePassword();
		if($res==1) {
			$this -> load -> view('header');
			$succ["validpass"] = "Your password is successfully changed.";
			$this -> load -> view('settings',$succ);

		} 	
		if($res==-1) {
			$this -> load -> view('header');
			$Error["Invalid1"] = "Incorrect current password";
			$this -> load -> view('settings',$Error);
		}
		if($res==0) {
			$this -> load -> view('header');
			$Error["Invalid1"] = "Passwords doesn't match.";
			$this -> load -> view('settings',$Error);
		}

	}

	public function editProfile() {
	$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
$this->output->set_header("Pragma: no-cache");
		$Error["Invalid"] = NULL;
		$this -> load -> helper('url');
		$this -> load -> library('session');
		$this -> load -> library("form_validation");
		$this -> form_validation -> set_rules("name", "Name", "required|trim|min_length[4]|xss_clean");
		$this -> form_validation -> set_rules("class", "Class", "required|numeric|trim|xss_clean");

		if ($this -> form_validation -> run() == FALSE) {
			$this -> load -> view('header');
			$this->load->view("settings", $Error);
		}
		else if($_POST['class']!=9 && $_POST['class']!='10')
		{
			$Error["Invalid"] = "Invalid class. Please type class '9' or '10'";
			$this -> load -> view('header');
			$this->load->view("settings",$Error);
		}
		else {
			$this -> load -> model("registermodel");
			$res = $this -> registermodel -> editProfile();
			$success["valid1"] = "Your settings are successfully updated!";
			$this -> load -> view('header');
			$this -> load -> view("settings",$success);
			//redirect('home/login');
		}
		

	}
	
	function do_upload()
	{
		
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
		$this->output->set_header("Pragma: no-cache");
		$this -> load -> view('header');
		$config['upload_path'] = $_SERVER['DOCUMENT_ROOT'] . "/StudyChem/submission";
		$config['allowed_types'] = 'doc|pdf';
		$config['max_size']	= '2048';
		
		
		$assgname1 =  $_POST['assgname1'];
		//echo $assgname1;
		$email = $this->session->userdata('email');
		$this -> db -> where('student',$email);
		$data1 = $this -> db -> get('teacherClass');
		
		$data = $data1-> row();
		$teacher = $data->teacher;
		$subdate =  $_POST['subdate1'];
		
		$this->load->library('upload', $config);

		$query = $this->db->get_where('submission', array('assgname' => $assgname1, 'student' =>$email)); 
        $count= $query->num_rows();    //counting result from query
		if ($count > 0){
				if ( !$this->upload->do_upload() )
				{
				$senddata = array('error' => $this->upload->display_errors());
				}
				else{
				//$sendData['title'] = "Assignment already added";
				$sdata = $this->upload->data();
				$senddata = array('upload_data' => $this->upload->data());
				$sendData['title1'] = "Assignment is successfully submitted!";
				$sdata = $this->upload->data();
				$path = $sdata['file_path'] .$sdata['file_name']; 
				$this -> db -> where('teacher',$teacher);
				$this -> db -> where('assgname',$assgname1);
				$this -> db -> where('student',$email);
				
				$array1 = array('assgname' => $assgname1, 'teacher' => $teacher,
				'student' => $email, 'subdate' => $subdate ,'path' => $path
				);
				
				$this -> db -> update('submission',$array1);
				$sendData['title'] = "Assignment is successfully submitted!";
				
				$student = $this -> session -> userdata('email');
				$this -> db -> where('student',$student);
				$data = $this -> db -> get('teacherClass');
				$data = $data -> row();
				$teacher = $data -> teacher;
				$this -> db -> where('teacher',$teacher);
				$this -> db -> where('assgname',$assgname1);
				
				
				
				
				$topics = $this -> db -> get('submission');
				$topics = $topics -> row();
				$notify = array('student' => $student,
        				'teacher' => $teacher,
        				'assgname' => $topics -> assgname);
					$this -> db -> insert('submitnot',$notify);
				}
		}
		
		if ($count == 0)
		{ 
			if ( !$this->upload->do_upload() )
			{
				$senddata = array('error' => $this->upload->display_errors());
			}
			else
			{
				$sdata = $this->upload->data();
				$senddata = array('upload_data' => $this->upload->data());
				//$sendData['title1'] = "Assignment is successfully submitted!";
				$sdata = $this->upload->data();
				$path = $sdata['file_path'] .$sdata['file_name']; 
				$array = array('assgname' => $assgname1, 'teacher' => $teacher,
				'student' => $email, 'subdate' => $subdate ,'path' => $path
				);
				$this -> db -> insert('submission',$array);
				$sendData['title'] = "Assignment is successfully submitted!";
				
				$student = $this -> session -> userdata('email');
				$this -> db -> where('student',$student);
				$data = $this -> db -> get('teacherClass');
				$data = $data -> row();
				$teacher = $data -> teacher;
				$this -> db -> where('teacher',$teacher);
				$this -> db -> where('assgname',$assgname1);
				
				$topics = $this -> db -> get('submission');
				$topics = $topics -> row();
				$notify = array('student' => $student,
        				'teacher' => $teacher,
        				'assgname' => $topics -> assgname);
					$this -> db -> insert('submitnot',$notify);
        
				}
		}
		
		
		$this -> db -> where('teacher',$teacher);
		$data = $this -> db -> get('addassignment');
		$sendData['data'] = $data;
		//$this -> load -> view('assignments',$sendData);
		
		$this -> db -> where('teacher',$teacher);
		$this -> db -> where('student',$email);
		$data = $this -> db -> get('submission');
		$sendData['data2'] = $data;
		$this -> load -> view('assignments',$sendData);
		
		
	}
	
	public function viewself()
	{
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
		$this->output->set_header("Pragma: no-cache");
		
		$this -> load -> view('header');
		$stdemail = $this -> session -> userdata('email');
		$senddata['name'] = $stdemail;
		
		$this->db->where('email',$stdemail);
		$data = $this->db->get('student');
		$row = $data->row();
		$senddata['name'] = $row->name;
		$senddata['class'] = $row->class;
		$this->db->where('student',$stdemail);
		$data2 = $this->db->get('submission');
		$senddata['data2'] = $data2;
		
		$this->db->where('student',$stdemail);
		$data3 = $this->db->get('stuhasattend');
		$senddata['data3'] = $data3;
		
		$datent = "0000-00-00";

		$this->db->where('date1 !=',$datent);
		$this->db->where('student',$stdemail);
		//$this->db->where('value', 1);
		$this->db->select('Monthname(date1) as month1, SUM(CASE WHEN value = 1 THEN 1 ELSE 0 END ) as total1 ,SUM(CASE WHEN value = 0 THEN 1 ELSE 0 END ) as total2,COUNT(Month(date1)) as total3');
		
		$this->db->group_by('month1'); 
		//$this->db->order_by('total1', 'desc');
		
		$query = $this->db->get('stuhasattend'); 
		
		
		$this->db->where('student',$stdemail);
		$results = $this->db->get('results');
		$senddata['results'] = $results;
		
		//$this->db->where('student',$stdemail);
		//$this->db->select('Monthname(date1) as month2, COUNT(Month(date1)) as total2');
		//$this->db->from('stuhasattend b');
		//$this->db->group_by('month2'); 
		//$this->db->order_by('total2', 'desc');
		//$this->db->join('a', 'a.month1 = b.month2');
		
		//SELECT Monthname(date1) as month1,
       //Count(CASE WHEN up = 1 THEN 1 ELSE 0 END) AS UpCount,
       //Count(CASE WHEN down = 2 THEN 1 ELSE 0 END) AS DownCount
    //FROM stuhasattend
    //GROUP BY 'month1'
		
		
			
			
		$senddata['data4'] = $query;
		
			
			$query = $this->db->get('stuhasattend'); 
			
			
		//$senddata['data5'] = $query;
		
		$this -> load -> view('header');
		$this -> load -> view('teacher/viewStudent',$senddata);
	
	}
	public function analysis($ad,$qid,$email)
	{
		$error = null;
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
		$this->output->set_header("Pragma: no-cache");
		//$email = $this -> session -> userdata('email');
		$quizid = $qid;
		
		$this->db->where('quizid',$quizid);
		$data = $this -> db -> get('problem');
		$senddata['data'] = $data;
		$senddata['student'] = $email;
		$senddata['quizid'] = $quizid;
		
		$this -> load -> view('header');
		$this -> load -> view('analysis',$senddata);
	}
	
	public function deleteUploadNotification($id,$email)
	{
		$this -> db -> where('id',$id);
		$this -> db -> delete('uploadnot');
		$this->assignments();
		//redirect('teacher');
	}
	
	public function deleteTopicNotification($id,$email)
	{
		$this -> db -> where('id1',$id);
		$this -> db -> delete('topicupload');
		//$this->assignments();
		redirect('class9');
	}
	
	public function timeline() 
	{
		$this->output->set_header("Cache-Control: no-store, no-cache, must-revalidate, no-transform, max-age=0, post-check=0, pre-check=0");
$this->output->set_header("Pragma: no-cache");
		$email = $this -> session -> userdata('email');
		$this -> db -> where('student',$email);
		$data = $this -> db -> get('teacherClass');
		if($data -> num_rows() == 1) 
		{
			$row = $data -> row();
			$teacher = $row -> teacher;
			$sendData['data'] = $teacher;
			$this -> load -> view('header');
			$this -> load -> view('timeline',$sendData);
		}
	}
	
	
}