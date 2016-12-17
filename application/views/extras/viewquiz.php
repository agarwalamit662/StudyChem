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
<div class="content">
    <div class="container"> 
        <div class="page-header">
          <center>  <h2>Quiz Board</h2> </center>
        </div>
        <div class="page-header">
        <h2> Assignments </h2>
        </div>
        <?php if($quiz -> num_rows() == 0) { ?>
        <div class="alert alert-info"> 
        <p> Your teacher have not hosted any quiz </p>
        </div>
        <?php } else {?>
        <table class="table table-striped table-hover">
        <thead>
                <tr>
                  <th>#</th>
                  <th>Quiz name</th>
                  <th>Description</th>
                  
				  <th>Duration</th>
				  <th>Start Date</th>
				  <th>End Date</th>
				
				</tr>
        </thead>
        <tbody>
    
		<?php
		$qno = 1;
        $i=1;
        foreach($quiz -> result() as $row) { 
			$quizid = $row -> quizid;
      		$quizname = $row -> quizname;
            $quizdes = $row -> quizdes;
            
            
			$startdate = $row -> startdate;
            $enddate = $row -> enddate;
            $this ->db->where('quizid',$quizid);
           // $this ->db->where('time_left',$timer->row()->time_left);
          
			//var_dump($timer);
			$result = $this ->db->get('timer');
			$duration = $result ->row()->time_left;
			//echo $duration;
			?>
            
            <tr class="info">
                <td><?php echo $i; $i++;?> </td>
                <td> <a href="<?php echo site_url('student/quiz/'.$qno.'/'.$quizid.'/');?>" >  <?php echo $quizname;?> </a></td>
                <td> <?php echo $quizdes;?> </td>
                
                <td> <?php echo $duration;?> </td>
                <td> <?php echo $startdate;?> </td>
				<td> <?php echo $enddate;?> </td>
				<td>
				 
            </tr>
            
        <?php } ?>
        </tbody>
        </table>
        <?php } ?>

   
</div>
</div>
<div>
</div>
       
    <script type="text/javascript" src="<?php echo base_url('assests/js/jquery.min.js'); ?>"></script>
    <script type="text/javascript" src="<?php echo base_url('assests/js/bootstrap.min.js'); ?>"></script>
    <script type="text/javascript" src="<?php echo base_url('assests/js/boot-business.js'); ?>"></script>
       