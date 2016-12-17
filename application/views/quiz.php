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
	
	<?php  $time_left = $quiz->row()-> time_left; ?>
  </head>
  <body>
    <!-- Start: HEADER -->
    
    <!-- End: HEADER -->
    <!-- Start: MAIN CONTENT -->
    
	
	<div class="content">
    <div class="container"> 
        <div class="page-header">
            <h2>Quiz Title</h2>
			        </div>      
		<div>
		<input type="hidden" id="qid" value="<?php echo $quizid?>" />
		<!-- time in seconds        < ?php echo base_url();? >student/submitquiz-->
			<div id="timer"><?php  echo $quiz->row()-> time_left; ?></div>
		<?php
        $i=1;
        $row = $data -> row();
		if($data -> num_rows() ==0 ){?>
			<?php echo "No questions for this Quiz" ?>
		<?php }
		else { 
      		$qno = $row -> ques_num;
            $ques = $row -> ques;
            $opta = $row -> opta;
            $optb = $row -> optb;
            $optc = $row -> optc;
            $optd = $row -> optd;
            $marks = $row -> marks;
			$quizid = $row -> quizid;
			
			?>
	<h3 id='qno' data-val="<?php echo $qno?>">Question No- <?php echo $qno?></h3>
	
	<br>
	
	<h4>Question: <?php echo $ques?></h4>
	<div class = "table-responsive">
			<form action = "" method="post">

	<table class = "table">
		<tr>
			<td>
				<h5><span class="label label-info">A</span>
				<input type="radio" id = "a" name = op class="options" value ="<?php echo $opta?>">
				<?php echo $opta?></h5>
			</td>
			<td>
				<h5><span class="label label-info">B</span>
				<input type="radio" id = "b" name = op class="options" value ="<?php echo $optb?>">
				<?php echo $optb?></h5>
			</td>
		</tr>
		
		<tr>
			<td>
				<h5><span class="label label-info">C</span>
				<input type="radio" id = "c" name = op class="options" value ="<?php echo $optc?>">
				<?php echo $optc?></h5>
			</td>
			<td>
				<h5><span class="label label-info">D</span>
				<input type="radio" id = "d" name = op class="options" value ="<?php echo $optd?>">
				<?php echo $optd?></h5>
			</td>
		</tr>
		<tr>
		<td>
		<button type="button" id = "b1" style = "border-radius: 10%; width: 100px;height:50px;" onclick  = "savefunction()" class="" ><a href="">Mark </a></button>
		</td>
		<td>
		<button type="button" id = "b2" style = "border-radius: 10%; width: 100px;height:50px;" onclick  = "submit1()" class="" >End Quiz</button>	
		</td>
		</tr>
	</table>
	</form>
		
	<ul class="pager">
		<form action="<?php echo base_url();?>sxcxc" method="post" class="">
		<?php 
		$prev = 1;
		if($qno > 1) {$prev = $qno - 1; ?>
		
		<li class="previous"><a href="<?php echo site_url('student/quiz/'.$prev.'/'.$quizid.'/');?>" >&larr; Previous</a></li>
		<?php } ?>
		</form>
		
		
		<form action="<?php echo base_url();?>student/editProfile" method="post" class="">
		<?php if($qno < $count) {$next = $qno + 1;  ?>
		<li class="next" ><a href="<?php echo site_url('student/quiz/'.$next.'/'.$quizid.'/');?>" onclick="" >Next &rarr;</a></li>
		<?php } ?>
		</form>
		<?php } ?>
		
	</ul>
	</div>        
		</div>
	
	</div>
    </div>
	<div class = "content" style = "height: 200px;"></div>
      <script type="text/javascript" src="<?php echo base_url('assests/js/jquery.min.js'); ?>"></script>
    <script type="text/javascript" src="<?php echo base_url('assests/js/bootstrap.min.js'); ?>"></script>
    <script type="text/javascript" src="<?php echo base_url('assests/js/boot-business.js'); ?>"></script>
	<script>
	
	function savefunction(){
	
	if(document.getElementById("a").checked){
	
	
		$.get('/StudyChem/student/submitquiz/'+$('#qno').data('val')+'/'+$('#qid').val()+'/a',function(e){
		if(!e){
		alert('Sumbitted')
		}
		});
	}
	else if(document.getElementById("b").checked){
		$.get('/StudyChem/student/submitquiz/'+$('#qno').data('val')+'/'+$('#qid').val()+'/b',function(e){
		if(!e){
		//alert('Sumbitted')
		}
		});
	}
	else if(document.getElementById("c").checked){
		$.get('/StudyChem/student/submitquiz/'+$('#qno').data('val')+'/'+$('#qid').val()+'/c',function(e){
		if(!e){
		//alert('Sumbitted')
		}
		});
	}
	else if(document.getElementById("d").checked){
		$.get('/StudyChem/student/submitquiz/'+$('#qno').data('val')+'/'+$('#qid').val()+'/d',function(e){
		if(!e){
		//alert('Sumbitted')
		}
		});
	}
	else{
		alert("Please Select one of the options");
	}
	}
	
	$(function(){
	l = localStorage;
	
		if($('#timer').html()=='0'){
		l.clear();
		window.location="/StudyChem/student/endquiz/"+$('#qid').val();
		}
		console.log(l.getItem('timer'))
	 if(l.getItem('timer')&& l.getItem('timer') !== "NaN"){
		  $('#timer').html(l.getItem('timer'));
	 }
	
	if(l.getItem($('#qno').data('val')))
	$('#'+l.getItem($('#qno').data('val'))).attr('checked',true)
	$('.options').on('click',function(e){
		console.log($('#qno').data('val'),$(this).attr('id'));
		l.setItem($('#qno').data('val'),$(this).attr('id'))
	})
	});
	a = setInterval(function(){
	$('#timer').html($('#timer').html()-1);
	l.setItem('timer',$('#timer').html());
	
	if($('#timer').html() <= "1"){
	l.clear();
	clearInterval(a);
	l.clear();
	//window.location="/StudyChem/student/endquiz/";
	
	$.get('/StudyChem/student/settime/'+'0'+'/'+$('#qid').val(),function(e){
		if(!e){
		window.location="/StudyChem/student/endquiz/"+$('#qid').val();
		alert('Time Over')
		}
		});
	}
	},1000)
	
	function submit1(){
	l.clear();
	clearInterval(a);
	l.clear();
	//window.location="/StudyChem/student/endquiz/";
	
	$.get('/StudyChem/student/settime/'+'0'+'/'+$('#qid').val(),function(e){
		
		if(!e){
		window.location="/StudyChem/student/endquiz/"+$('#qid').val();
		alert('Quiz Submitted Sucessfully')
		}
		});
	}
	</script>
    </body>
    </html>