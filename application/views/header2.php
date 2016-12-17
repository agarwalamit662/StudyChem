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
	
  </head>
  <body>
    <!-- Start: HEADER -->
    <header>
      <!-- Start: Navigation wrapper -->
      <div class="navbar navbar-fixed-top">
        <div class="navbar-inner">
          <div class="container">
            <a href="<?php echo site_url('home'); ?>" class="brand brand-bootbus">StudyChem</a>
            <!-- Below button used for responsive navigation -->
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <!-- Start: Primary navigation -->
            <div class="nav-collapse collapse">        
              <ul class="nav pull-right">
                <li class="dropdown">
                  <a href="<?php echo site_url('home'); ?>"> Home </a>                    
                </li>
                <li class="dropdown">
                  <a href="<?php echo base_url();?>home/about"> About </a>
                  
                </li>
                <li ><a href="<?php echo base_url();?>home/faq">FAQ</a></li>
                <li><a href="<?php echo base_url();?>home/contact">Contact us</a></li>
                <?php if($this -> session -> userdata('isLoggedIn') == FALSE)  { ?>
                <li><a href="<?php echo base_url();?>home/signup">Sign up</a></li>
                <?php } ?>
                <?php if($this->session->userdata("roleid")==1 || $this->session->userdata("roleid")==2) {?>
					
				<li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">My Profile<b class="caret"></b></a>

                <ul class="dropdown-menu">
                <?php if($this->session->userdata("roleid")==2) { ?>
                  <li><a href="<?php echo base_url();?>teacher/dashboard">Dashboard</a></li>
                  <?php } else {?>
                  <li><a href="<?php echo base_url();?>student/viewself">Dashboard</a></li>
                  <?php } ?>
                  <?php if($this->session->userdata("roleid")==2) { ?>
                  <li><a href="<?php echo base_url();?>teacher/timeline">News Feed</a></li>
                  <?php } ?>
                  <?php if($this->session->userdata("roleid")==1) { 
                   $email =  $this -> session -> userdata('email');
                   $this -> db -> where('student',$email);
                   $result = $this -> db -> get('teacherClass');
                   if ($result -> num_rows() == 1 ) {
                    ?>
                  <li><a href="<?php echo base_url();?>student/timeline">News Feed</a></li>
                  <?php 
                  } 
                  } 
                  ?>
                  <?php if($this->session->userdata("roleid")==2) { ?>
                  <li><a href="<?php echo base_url();?>teacher/quiz">Quizzes</a></li>
                  <?php } else if($this->session->userdata("roleid")==1) { $qsn =1; ?>
				  
				  <li><a href="<?php echo site_url('student/viewquiz/');?>">Quizzes</a></li> <?php } ?>
				  
				  <?php if($this->session->userdata("roleid")==2) { ?>
                  <li><a href="<?php echo base_url();?>teacher/assignments">Assignments</a></li>
                  <?php } else if($this->session->userdata("roleid")==1) { ?>
				  <li><a href="<?php echo base_url();?>student/assignments">Assignments</a></li> <?php } ?>
				  
				  <?php if($this->session->userdata("roleid")==2) { ?>
                  <li><a href="<?php echo base_url();?>teacher/attendance">Attendance</a></li>
                  <?php } ?>
                  
                  <li><a href="<?php echo base_url();?>student/settings">Settings</a></li>
                  <li><a href="<?php echo base_url();?>home/logout">Log out</a></li>

                  </ul>
                </li> <?php } else {?>

                <li><a href="<?php echo base_url();?>home/login">Log in</a></li> <?php } ?>
              <?php if($this->session->userdata("roleid")==2) { ?>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown">Notification<b class="caret"></b></a>                   
                  <ul class="dropdown-menu">
                    <?php 
                    $this -> db -> where('teacher',$this -> session -> userdata('email'));
                    $data = $this -> db -> get('notification');

                    foreach($data -> result() as $row) {
                      $student = $row -> student;
                      $this -> db -> where('email',$student);
                      $x = $this -> db -> get('student');
                      $x = $x -> row();
                      $name = $x -> name;
                      ?>
                      <li><a href="<?php echo base_url();?>teacher/deleteNotification/<?php echo $row -> id;?>/"> <?php echo $name;?> has viewed <?php echo $row -> topic;?></a> </li>
                    <?php }
                    ?>
					<?php 
                    $this -> db -> where('teacher',$this -> session -> userdata('email'));
                    $data1 = $this -> db -> get('submitnot');

                    foreach($data1 -> result() as $row) {
                      $student = $row -> student;
                      $this -> db -> where('email',$student);
                      $x = $this -> db -> get('student');
                      $x = $x -> row();
                      $name = $x -> name;
					  $email = $x -> email;
					  $assgname= $row -> assgname;
                      ?>
                      <li><a href="<?php echo base_url();?>teacher/deleteSubNotification/<?php echo $row -> id;?>/<?php echo $email;?>"> <?php echo $name;?> has submitted <?php echo $row -> assgname;?></a> </li>
                    <?php }
                    ?>
					
                  </ul>
                </li>
                <?php } ?>
				<?php if($this->session->userdata("roleid")==1) { ?>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown">Notification<b class="caret"></b></a>                   
                  <ul class="dropdown-menu">
                    <?php 
                    $this -> db -> where('student',$this -> session -> userdata('email'));
                    $data = $this -> db -> get('uploadnot');

                    foreach($data -> result() as $row) {
                      $teacher = $row -> teacher;
                      $this -> db -> where('email',$teacher);
                      $x = $this -> db -> get('student');
                      $x = $x -> row();
                      $name = $x -> name;
					  $assgname= $row -> assgname;
                      ?>
                      <li><a href="<?php echo base_url();?>student/deleteUploadNotification/<?php echo $row -> id;?>/<?php echo $email;?>"> <?php echo $name;?> has uploaded assignment "<?php echo $assgname;?>"</a> </li>
                    <?php }
                    ?>
					<?php 
                    $this -> db -> where('student',$this -> session -> userdata('email'));
                    $data = $this -> db -> get('topicupload');
					
					
                    foreach($data -> result() as $row) {
                      $teacher = $row -> teacher;
                      $this -> db -> where('email',$teacher);
                      $x = $this -> db -> get('student');
                      $x = $x -> row();
                      $name = $x -> name;
					  $id= $row -> id1;
					  $idmain= $row -> id;
					  $this -> db -> where('id',$idmain);
					  $result = $this-> db->get('topic');
					  $topic  = $result->row()->topic;
                      ?>
                      <li><a href="<?php echo base_url();?>student/deleteTopicNotification/<?php echo $row -> id1;?>/<?php echo $email;?>"> <?php echo $name;?> has uploaded topic "<?php echo $topic;?>"</a> </li>
                    <?php }
                    ?>
					
                  </ul>
                </li>
                <?php } ?>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <!-- End: Navigation wrapper -->   
    </header>
    </body>
    </html>