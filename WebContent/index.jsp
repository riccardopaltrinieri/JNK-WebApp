<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<div>
		<c:choose>
			<c:when test="${user == null}">
				<a href="${pageContext.request.contextPath}/LogIn">Log In</a>
			</c:when>
			<c:when test="${user != null}">
				<a href="${pageContext.request.contextPath}/LogOut">Log Out</a>
			</c:when>
		</c:choose>
	</div>
	<h1 align="center">Illegal Junkyard Crowdsourcing</h1>
</body>
</html>