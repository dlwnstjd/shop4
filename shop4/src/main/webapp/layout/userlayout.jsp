<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath }"/>
<!DOCTYPE html>
<html lang="en">
<title><decorator:title /></title>
<script type="text/javascript" 
	src="http://www.chartjs.org/dist/2.9.3/Chart.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link rel="stylesheet" href="https://www.w3schools.com/lib/w3-theme-black.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<style>
html,body,h1,h2,h3,h4,h5,h6 {font-family: "Roboto", sans-serif;}
.w3-sidebar {
  z-index: 3; 
  width: 250px;
  top: 43px;
  bottom: 0;
  height: inherit;
}
</style>
<script type="text/javascript"
	src="http://cdn.ckeditor.com/4.5.7/full/ckeditor.js"></script>
<decorator:head />
<link rel="stylesheet" href="${path}/css/main.css">
<body>

<!-- Navbar -->
<div class="w3-top">
  <div class="w3-bar w3-theme w3-top w3-left-align w3-large">
    <span style="float:right">
		<c:if test="${empty sessionScope.loginUser}">
			<a href="${path}/user/login.shop">로그인</a>
			<a href="${path}/user/userEntry.shop">회원가입</a>
		</c:if>
		<c:if test="${!empty sessionScope.loginUser}">
			${sessionScope.loginUser.username}님&nbsp;
			<a href="${path}/user/logout.shop">로그아웃</a>
		</c:if></span>
  </div>
</div>

<!-- Sidebar -->
<nav class="w3-sidebar w3-bar-block w3-collapse w3-large w3-theme-l5 w3-animate-left" id="mySidebar">
  <a href="javascript:void(0)" onclick="w3_close()" class="w3-right w3-xlarge w3-padding-large w3-hover-black w3-hide-large" title="Close Menu">
    <i class="fa fa-remove"></i>
  </a>
  <h4 class="w3-bar-item"><b>Menu</b></h4>
  <a class="w3-bar-item w3-button w3-hover-black" href="${path}/user/main.shop">회원관리</a>
  <a class="w3-bar-item w3-button w3-hover-black" href="${path}/item/list.shop">상품관리</a>
  <a class="w3-bar-item w3-button w3-hover-black" href="${path}/board/list.shop">게시판</a>
  <a class="w3-bar-item w3-button w3-hover-black" href="${path}/chat/chat.shop">채팅</a>

	<div class="w3-container">
		<%--ajax을 통해 얻은 환율 정보 내용 출력 --%>
		<div id="exchange1">
		</div>
	</div>
	<div class="w3-container">
		<%--KEB하나은행 정보 출력하기:
		USD, JPY, EUR, CNY: 매매기준율, 현찰사실때, 현찰파실때 --%>
		<div id="exchange2">
		</div>
	</div>
</nav>

<!-- Overlay effect when opening sidebar on small screens -->
<div class="w3-overlay w3-hide-large" onclick="w3_close()" style="cursor:pointer" title="close side menu" id="myOverlay"></div>

<!-- Main content: shift it to the right by 250 pixels when the sidebar is visible -->
<div class="w3-main" style="margin-left:250px">

  <div class="w3-row w3-padding-64">
  
	<div class="w3-row-padding w3-margin-bottom">
		<div class="w3-half">
			<div class="w3-container w3-padding-16">
				<div id="piecontatiner" style=" width:80%; border: 1px solid #000000">
				<canvas id="canvas1" style="width:100%;"></canvas>
				</div>			
			</div>
		</div>
		<div class="w3-half">
			<div class="w3-container w3-padding-16">
				<div id="barcontatiner" style=" width:80%; border: 1px solid #000000">
				<canvas id="canvas2" style="width:100%;"></canvas>
				</div>			
			</div>
		</div>
	</div>
	
    <div class="w3-threequarter w3-container" style="text-align: left; vertical-align: top">
      <decorator:body/>
    </div>
    
  </div>
  


  <footer id="myFooter">
    <div class="w3-container w3-theme-l2 w3-padding-32">
      <h4>구디아카데미 Since 2016</h4>
    </div>

    <div class="w3-container w3-theme-l1">
      <p>Powered by <a href="http://www.gdu.co.kr/main/main.html" target="_blank">구디아카데미</a></p>
    </div>
  </footer>

<!-- END MAIN -->
</div>

<script>
// Get the Sidebar
var mySidebar = document.getElementById("mySidebar");

// Get the DIV with overlay effect
var overlayBg = document.getElementById("myOverlay");

// Toggle between showing and hiding the sidebar, and add overlay effect
function w3_open() {
  if (mySidebar.style.display === 'block') {
    mySidebar.style.display = 'none';
    overlayBg.style.display = "none";
  } else {
    mySidebar.style.display = 'block';
    overlayBg.style.display = "block";
  }
}

// Close the sidebar with the close button
function w3_close() {
  mySidebar.style.display = "none";
  overlayBg.style.display = "none";
}
</script>
<script type="text/javascript">
	var randomColorFactor = function(){
		return Math.round(Math.random() * 255);
	}
	var randomColor = function(opa){
		return "rgba(" + randomColorFactor() + ","
				+ randomColorFactor() + ","
				+ randomColorFactor() + ","
				+ (opa || '.3') + ")";
	}
	$(function(){
		piegraph();
		bargraph();
		exchangeRate1();	//환율정보를 ajax을 통해 크롤링하기.
		exchangeRate2();
	})
	
	function exchangeRate1(){
		$.ajax("${path}/ajax/exchange1.shop",{
			success: function(data){
				$("#exchange1").html(data);
			},
			error: function(e){
				alert("환율 조회시 서버 오류:" + e.status);
			}
		})
	}
	
	function exchangeRate2(){
		$.ajax("${path}/ajax/exchange2.shop",{
			success: function(data){
				$("#exchange2").html(data);
			},
			error: function(e){
				alert("환율 조회시 서버 오류:" + e.status);
			}
		})
	}
	
	function piegraph(){
		$.ajax("${path}/ajax/graph1.shop",{
			//data: json.toString()으로 전달받은 data
			//json형태의 문자열로 수신함
			success: function(data){
				pieGraphPrint(data);
			},
			error: function(e){
				alert("서버 오류:" + e.status);
			}
		})
	}
	function bargraph(){
		$.ajax("${path}/ajax/graph2.shop",{
			success: function(data){
				barGraphPrint(data);
			},
			error: function(e){
				alert("서버 오류:" + e.status);
			}
		})
	}
	function pieGraphPrint(data){
		console.log(data)
		//JSON.parse(data): json형태로 번역(parse)
		var rows = JSON.parse(data);
		var names = []
		var datas = []
		var colors = []
		$.each(rows,function(index,item){
			names[index] = item.name;
			datas[index] = item.cnt;
			colors[index] = randomColor(1);
		})
		var config = {
			type: 'pie',
			data: {
				datasets: [{
					data: datas,
					backgroundColor: colors
				}],
				labels: names
			},
			options: {
				responsive: true,
				legend: {position: 'top'},
				title:{
					display: true,
					text: '글쓴이 별 게시판 등록 건수',
					position: 'bottom'
				}
			}		
		}
		var ctx = document.getElementById("canvas1").getContext("2d");
		new Chart(ctx,config);
	}
	function barGraphPrint(data){
		console.log(data)
		var rows = JSON.parse(data);
		var dates = []
		var datas = []
		var colors = []
		$.each(rows,function(index,item){
			dates[index] = item.date;
			datas[index] = item.cnt;
			colors[index] = randomColor(1);
		})
		var config = {
				type: 'bar',
				data: {	labels: dates,
						datasets:[
						{
							type: 'line',
							borderColor: colors,
							borderWidth: 2,
							label: '건수',
							fill: false,
							data: datas
						},{
							type: 'bar',
							label: '건수',
							backgroundColor: colors,
							data: datas,
							borderWidth: 2
						}]
				},
				options:{
					responsive: true,
					title: {display: true,
							text: '일수 별 게시판 등록 건수',
							position: 'bottom'
					},
					legend: {display: false},
					scales:{
						xAxes:[{
							display: true,
							scaleLabel: {
								display: true,
								labelString: "게시물 작성자"
							},
							stacked: true	
						}],
						yAxes: [{
							display: true,
							scaleLabel: {
								display: true,
								labelString: "게시물 작성건수"
							},
							stacked: true	//기본값 0부터 시작
						}]
					}
				}
			};
			var ctx = document.getElementById("canvas2").getContext("2d");
			new Chart(ctx, config);
	}
</script>
</body>
</html>
