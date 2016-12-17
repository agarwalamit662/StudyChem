<html>
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
<div class="content">
    <div class="container"> 
        <div class="page-header">
          <center>  <h2>Quiz Summary</h2> </center>
        </div>
        <table class="table table-striped table-hover">
        <thead>
                <tr>
                  <th>#</th>
                  <th>Question</th>
				  <th>Option A</th>
				  <th>Option B</th>
				  <th>Option C</th>
				  <th>Option D</th>
				  <th>Answer Marked</th>
				  <th>Correct Answer</th>
				  <th>Marks Obtained</th>
				</tr>
        </thead>
        <tbody>
    
		<?php
		$qno = 1;
        $i=1;
		$totalnum = $data->num_rows();
		$totalattempt = $totalnum;
		$totalcorrect = 0;
		$totalincorrect = 0;
		$sum = 0;
        foreach($data -> result() as $row) {
			$qno = $row -> ques_num;
			$ques = $row -> ques;
      		$opta = $row -> opta;
      		$optb = $row -> optb;
      		$optc = $row -> optc;
      		$optd = $row -> optd;
            $correct = $row -> correctans;
			
            $this ->db->where('quizid',$quizid);
            $this ->db->where('quesid',$qno);
            $this ->db->where('student',$student);
			$result = $this ->db->get('studentquiz');
			$marks = 0;
			if($result ->num_rows === 0){
				$marked = 'NA';
				$totalattempt--;
				}
			else{
				$marked= $result ->row()->marked;
				$this ->db->where('quizid',$quizid);
				$this ->db->where('quesid',$qno);
				$this ->db->where('student',$student);
				$data2 = $this ->db->get('quiz_paper');
				$marks = $data2 ->row()->obtained;
				if($data2->row()->wr == 1){
				$totalcorrect++;
				}
				else{
				$totalincorrect++;
				}
			}
			?>
            
            <tr class="info">
                <td><?php echo $i; $i++;?> </td>
                <td> <?php echo $ques;?> </td>
                <td> <?php echo $opta;?> </td>
                <td> <?php echo $optb;?> </td>
                <td> <?php echo $optc;?> </td>
                <td> <?php echo $optd;?> </td>
                <td> <?php echo $marked;?> </td>
				<td> <?php echo $correct;?> </td>
				<td> <?php echo $marks;$sum = $sum+$marks?> </td>
				<td>
				 
            </tr>
            
        <?php } ?>
        </tbody>
        </table>
		<table class="table table-striped table-hover">
			<thead>
				<tr>
                  <th>Total number of Questions</th>
				  <th>Total Attemped</th>
				  <th>Correct</th>
				  <th>Incorrect</th>
				  <th>Total Marks Obtained</th>
				</tr>
			</thead>
			<tbody>
			
				<tr class = "info">
					<td><?php echo $totalnum;?></td>
					<td><?php echo $totalattempt;?></td>
					<td><?php echo $totalcorrect;?></td>
					<td><?php echo $totalincorrect;?></td>
					<td><?php echo $sum;?></td>
				</tr>
			</tbody>
		</table>

   
</div>
</div>
<div>
</div>
       
    <script type="text/javascript" src="<?php echo base_url('assests/js/jquery.min.js'); ?>"></script>
    <script type="text/javascript" src="<?php echo base_url('assests/js/bootstrap.min.js'); ?>"></script>
    <script type="text/javascript" src="<?php echo base_url('assests/js/boot-business.js'); ?>"></script>
       
</html>