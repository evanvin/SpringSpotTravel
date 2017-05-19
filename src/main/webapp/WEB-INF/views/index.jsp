<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
	<link rel="stylesheet" href="<c:url value="/static/resources/css/noUiSlider.css"/>">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.7/css/materialize.min.css">
	<script   src="https://code.jquery.com/jquery-3.1.0.min.js"   integrity="sha256-cCueBR6CsyA4/9szpPfrX3s49M9vUU5BgtiJj06wt/s="   crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.7/js/materialize.min.js"></script>
	<script src="<c:url value="/static/resources/js/noUiSlider.js"/>"></script>
	

	<style>
		body{
			background-image: url('<c:url value="/static/resources/gfx/image1.jpg"/>');
			height:100%;
		}
	</style>
	
	
	</head>


<body>
<div class="valign-wrapper" style="height: 100%;">
     <div class="valign" style="width:100%;">
         
		<div class="row">
			<div class="col s6 offset-s3">
			  <div class="card">
				<div class="card-content">
				  <span class="card-title">Adventure awaits you...</span>
				  
				  <form:form action="loadMap" commandName="adventureForm" id="form" onsubmit="return getInterests();">
				  <div class="row">
					  <div class="row">
						<div class="input-field col s6">
						  <form:input path="zip" id="zip" value="20910"/>	
						  <label for="zip">Zip</label>
						</div>
						<div class="input-field col s6">
							<form:hidden path="interests" id="interests" value="${interestObjects}"/>
							<select multiple id="interestSelect">
							  <option value="" disabled selected></option>
							</select>
						  	<%--
						  	<form:select path="interests" multiple="true" id="interests">
						  		<form:option value="" disabled="disabled" selected="selected"></form:option>
						  		<form:options items="${interestObjects}" itemLabel="interestName" itemValue="interestValue" />
						  	</form:select>
						  	 --%>
							<label for="interests">Interests</label>
						</div>
					  </div>
					  
					  <div class="row">
							<div class="col s12">
								<label for="searchRadius">Search Radius in Miles</label>
								<br><br>
								<p class="range-field">
									<div id="searchRadius" class="noUiSlider"></div>
								</p>
								<form:hidden path="innerBound" id="innerBound" value="50"/>
								<form:hidden path="outerBound" id="outerBound" value="150"/>
							</div>
						  
					  </div>
					  <br>
					 
					  <div class="row">
						<div class="input-field col s3">
							<form:select path="resultsWanted" id="results">
                                <form:option value="10">10</form:option>
                                <form:option value="20">20</form:option>
                                <form:option value="30">30</form:option>
                            </form:select>
							<label>Results</label>
						</div>
						<div class="input-field col s6">
						 	<button class="btn waves-effect waves-light" type="submit">Embark <i class="material-icons right">send</i></button>
						 <%--
						 <button class="btn waves-effect waves-light" id="load" type="button">Embark
							<i class="material-icons right">send</i>
						  </button>
						  <a href="testOne.do">Click me</a>
						  --%>
						</div>
					  </div>
				  </div>
				  
				  </form:form>
				</div>
			  </div>
			</div>
		  </div>		 
				
			
	
	</div>
</div>


 

<%--						
<script src="<c:url value="/static/resources/js/types.js"/>"></script>
--%>
 
<script>

$('#interestSelect').html($('#interests').val());
$('select').material_select();

	
	



	var slider = document.getElementById('searchRadius');
	  noUiSlider.create(slider, {
	   start: [50, 150],
	   connect: true,
	   step: 5,
	   range: {
		 'min': 0,
		 'max': 1000
	   },
	   format: wNumb({
		 decimals: 0
	   })
	  });
	  
	  
	slider.noUiSlider.on('change', function(){
		var radius = slider.noUiSlider.get();
		$('#innerBound').val(radius[0]);
		$('#outerBound').val(radius[1]);
	});


function getInterests(){
	var options = []; 
	$('#interestSelect :selected').each(function(i, selected){
		if($(selected).val() != '')
	  		options.push($(selected).val());	 
	});
	$('#interests').val(options);
	return true;
}
	

</script>
</body>
</html>