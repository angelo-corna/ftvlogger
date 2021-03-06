<%@ page import="java.sql.*, javax.sql.*, javax.naming.*, java.util.*" %>
<%@ page import="ftvlogger.RunDBStatement" %>
<html>
	<head>
		 <title>FTV logger</title>
		 <link rel="icon" href="favicon.ico" />
	<%

		RunDBStatement stm = new RunDBStatement();
	
		String month = request.getParameter("month");
		
		//Get Year Power Data
		String statement = "select substr(date,9,2),ftv_energy,con_energy from henergy where substr(date,1,7) = '"+month.substring(3)+"-"+stm.getMonthNum(month.substring(0,3))+"'";
		List<String> rs = stm.getResultSet("jdbc/ftvdb", statement);    
		StringBuilder sbDate = stm.getStringBuilder(rs, 0, "measure");
		StringBuilder sbFtvEnergy = stm.getStringBuilder(rs, 1, "measure");
		StringBuilder sbConEnergy = stm.getStringBuilder(rs, 2, "measure");
		
	%>	 
		 
        <!--Load the AJAX API-->
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
		<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript">
        
 	        tempDate="<%=sbDate.toString()%>";
	        var jsDate = new Array();
	        jsDate = tempDate.split(',','<%=rs.size()%>');
	
	        tempFtvEnergy="<%=sbFtvEnergy.toString()%>";
	        var jsFtvEnergy = new Array();
	        jsFtvEnergy = tempFtvEnergy.split(',','<%=rs.size()%>');
	
	        tempConEnergy="<%=sbConEnergy.toString()%>";
	        var jsConEnergy = new Array();
	        jsConEnergy = tempConEnergy.split(',','<%=rs.size()%>');
	
	        var totFtvEnergy = 0;
	        var totConEnergy = 0;
	        for (var i = 0; i < jsDate.length; i++) {
				totFtvEnergy = totFtvEnergy + Number(jsFtvEnergy[i]);
				totConEnergy = totConEnergy + Number(jsConEnergy[i]);
	        }
        
          // Load the Visualization API and the core chart package.
          google.load('visualization', '1.0', {'packages':['corechart']});

          // Set a callback to run when the Google Visualization API is loaded.
          google.setOnLoadCallback(drawChart);

          // Callback that creates and populates a data table,
          // instantiates the pie chart, passes in the data and
          // draws it.
          function drawChart() {

            // Create the today data table.
            var dataDays = new google.visualization.DataTable();
            dataDays.addColumn('string', 'Day');
            dataDays.addColumn('number', 'Production (KWh)');        
            dataDays.addColumn('number', 'Consumption (KWh)');  
            for (var i = 0; i < jsDate.length; i++) {
    			dataDays.addRow([ jsDate[i], Number(jsFtvEnergy[i]) , Number(jsConEnergy[i]) ]);
            }
            
            // Set year chart options
            var optionsDays = {
	           	'width':1430,
               	'height':480,
	    	    'legend' : { position: 'bottom' },
	    	    'connectSteps' : false,
	    	    'colors': ['orange', 'gray'],
            };
            
            // Instantiate and draw our chart, passing in some options.
            var chartDays = new google.visualization.ColumnChart(document.getElementById('chartDays'));
            function selectHandler() {
                var selection = chartDays.getSelection()[0].row;
                var day = dataDays.getValue(selection,0);
                var valueMonth = "<%=month%>";

    		 	var protocol = window.location.protocol;
    		 	var host = window.location.host;
    		 	var pathname = window.location.pathname;

    		 	window.open(protocol+'//'+host+'/'+pathname.split('/')[1]+'/detailedDailyProduction.jsp?date='+day+valueMonth,'_blank');
            }
            google.visualization.events.addListener(chartDays, 'select', selectHandler);   
            chartDays.draw(dataDays, optionsDays);

          }

		</script>
	</head>
	<body>
        <!--Divs that will hold the charts-->
        <table style="width:100%" bgcolor="#152934">
  			<tr>
  				<td><img src="logo.jpg" height="50" width="155"></td>
  				<td align="right">
					<a href="index.jsp" style="text-decoration:none"><font face="arial" size="3" color="#FFFFFF" >Dashboard</font></a>&nbsp;&nbsp;
				</td>
  				
	    	</tr>
    	</table>
        <table style="width:100%">
  			<tr><td>&nbsp;</td></tr>
  			<tr><td>&nbsp;</td></tr>
  			<tr>
    			<td align="center"><font face="arial" size="5" color="#000000" ><b> <%=stm.getMonthCom(month.substring(0,3))+" "+month.substring(3)%> Production and Comsumption</b></font></td>
	    	</tr>
  			<tr><td> </td></tr>
  			<tr>
    			<td align="center"><font face="arial" size="2" color="#000000" ><b>
    				<script type="text/javascript">
        				document.write(totFtvEnergy.toLocaleString())
      				</script>
	    			KWh produced - 
    				<script type="text/javascript">
        				document.write(totConEnergy.toLocaleString())
      				</script>
	    			KWh consumed</b></font>
	    		</td>
	    	</tr>
  			<tr>
    			<td><div id="chartDays"></div></td>
	    	</tr>
    	</table>
    	    	<br>
    	<hr>
        <table style="width:100%">
  			<tr>
				<td align="center">
					<a href="mailto:cornangelo@gmail.com" style="text-decoration:none"><font face="arial" size="2" color="#00000" >39 Software</font></a>&nbsp;&nbsp;
				</td>
	    	</tr>
    	</table>
    	
    	<script>
		  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
		  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
		  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
		  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
		
		  ga('create', 'UA-104914961-1', 'auto');
		  ga('send', 'pageview');
		
		</script>
	</body>
</html>