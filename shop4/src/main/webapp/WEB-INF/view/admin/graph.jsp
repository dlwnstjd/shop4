<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/jspHeader.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>BAR graph</title>
<script type="text/javascript" 
	src="http://www.chartjs.org/dist/2.9.3/Chart.min.js"></script>
</head>
<body>
<div id="canvas-holder" style="width:100%; height:100%;">
	<canvas id="chart-area" width="100%" height="100%"></canvas>
</div>
<script type="text/javascript">
	var randomColorFactor = function(){
		return Math.round(Math.random() * 255);
	}
	//rgb:
		var randomColor = function(opacity){
		return "rgba(" + randomColorFactor() + ","
				+ randomColorFactor() + ","
				+ randomColorFactor() + ","
				+ (opacity || '.3') + ")";
	};
	var color = randomColor(1)
	var config = {
		type: 'bar',
		data: {
			datasets: [{
				data:[<c:forEach items="${map}" var="m">"${m.value}",</c:forEach>],
				backgroundColor:[
					<c:forEach items="${map}" var="m">randomColor(1),</c:forEach>],
			}],
			labels:
				[<c:forEach items="${map}" var="m">"${m.key}",</c:forEach>]
		},
		options:{
			responsive:true,
			legend: {display: false},
			title: {display:true, test: '게시판 작성자 분석'},
			scales: {
				yAxes: [{
					ticks: {
						beginAtZero: true
					}
				}]
			}
		}
	};
	window.onload = function(){
		var ctx = document.getElementById("chart-area").getContext("2d");
		new Chart(ctx, config);
	}
</script>
</body>
</html>