<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/jspHeader.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>메일 보내기</title>
<script type="text/javascript">
	function idinputchk(f){
		if(f.naverid.value==""){
			alert("네이버 아이디를 입력하세요");
			f.naverid.focus();
			return false;
		}
		if(f.naverpw.value==""){
			alert("네이버 비밀번호를 입력하세요");
			f.naverpw.focus();
			return false;
		}
		return true;
	}
</script>
</head>
<body>
<h2>메일보내기</h2>
<form name="mailform" method="post" action="mail.shop" 
	enctype="multipart/form-data" onsubmit="return idinputchk(this)">
	본인 네이버ID: <input type="text" name="naverid">
	본인 네이버PW: <input type="password" name="naverpw">
<table>
	<tr><td>보내는사람</td><td>${loginUser.email}</td></tr>
	<tr><td>받는사람</td>
	<%-- 홍길동 <asda@aaa.bbb>, --%>
	<td><input type="text" name="recipient" size="100" value=
	'<c:forEach items="${list}" var="user">
	${user.username} &lt;${user.email}&gt;,</c:forEach>'>
	</td></tr>
	<tr><td>제목</td><td><input type="text" name="title" size="100"></td></tr>
	<tr><td>메시지형식</td>
	<td><select name="mtype">
		<option value="text/html; charset=utf-8">HTML</option>
		<option value="text/plain; charset=utf-8">TEXT</option>
	</select></td></tr>
	<tr><td>첨부파일1</td><td><input type="file" name="file1"></td></tr>
	<tr><td>첨부파일2</td><td><input type="file" name="file1"></td></tr>
	<tr><td colspan="2">
		<textarea name="contents" cols="120" rows="10"></textarea>
		<script>CKEDITOR.replace("contents")</script></td></tr>
	<tr><td colspan="2"><input type="submit" value="메일보내기"></td></tr>
</table>
</form>
</body>
</html>