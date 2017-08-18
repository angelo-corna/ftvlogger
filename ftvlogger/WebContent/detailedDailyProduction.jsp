<%@ page import="java.sql.*, javax.sql.*, javax.naming.*, java.util.*" %>
<%@ page import="ftvlogger.RunDBStatement" %>
<html>
	<head>
		 <title>FTV logger</title>
		 <link rel="icon" href="favicon.ico" />
	<%

	RunDBStatement stm = new RunDBStatement();

	String dataP = request.getParameter("date");
	String day = dataP.substring(0,2);
	String month = dataP.substring(2,5);
	String year = dataP.substring(5);
	String completeDate = day+' '+stm.getMonthCom(month)+' '+year;

	//Get Today Power Data
		String statement = "select date,ftv_power,ftv_energy,con_power,con_energy from rs485data where date(date) = '"+year+"-"+stm.getMonthNum(month)+"-"+day+"'";
		List<String> rs = stm.getResultSet("jdbc/ftvdb", statement);    
		StringBuilder sbDate = stm.getStringBuilder(rs, 0, "date");
		StringBuilder sbFtvPower = stm.getStringBuilder(rs, 1, "measure");
		StringBuilder sbFtvEnergy = stm.getStringBuilder(rs, 2, "measure");
		StringBuilder sbConPower = stm.getStringBuilder(rs, 3, "measure");
		StringBuilder sbConEnergy = stm.getStringBuilder(rs, 4, "measure");

	%>	 
		 
        <!--Load the AJAX API-->
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
		<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript">

        
          // Load the Visualization API and the core chart package.
          google.load('visualization', '1.0', {'packages':['corechart']});

          // Set a callback to run when the Google Visualization API is loaded.
          google.setOnLoadCallback(drawChart);

          // Callback that creates and populates a data table,
          // instantiates the pie chart, passes in the data and
          // draws it.
                      
	        tempDate="<%=sbDate.toString()%>";
    	    var jsDate = new Array();
            jsDate = tempDate.split(',','<%=rs.size()%>');

            tempFtvPower="<%=sbFtvPower.toString()%>";
            var jsFtvPower = new Array();
            jsFtvPower = tempFtvPower.split(',','<%=rs.size()%>');

            tempFtvEnergy="<%=sbFtvEnergy.toString()%>";
            var jsFtvEnergy = new Array();
            jsFtvEnergy = tempFtvEnergy.split(',','<%=rs.size()%>');

            tempConPower="<%=sbConPower.toString()%>";
            var jsConPower = new Array();
            jsConPower = tempConPower.split(',','<%=rs.size()%>');

            tempConEnergy="<%=sbConEnergy.toString()%>";
            var jsConEnergy = new Array();
            jsConEnergy = tempConEnergy.split(',','<%=rs.size()%>');
            
	        ftvEnergyToday = Number(jsFtvEnergy[jsDate.length-1]) - Number(jsFtvEnergy[0]);
			conEnergyToday = Number(jsConEnergy[jsDate.length-1]) - Number(jsConEnergy[0]);

          
          function drawChart() {
   
            // Create the today data table.
            var dataToday = new google.visualization.DataTable();
            dataToday.addColumn('string', 'Data');
            dataToday.addColumn('number', 'Production (W)');        
            dataToday.addColumn('number', 'Consumption (W)');  


            for (var i = 0; i < jsDate.length; i++) {
    			dataToday.addRow([ jsDate[i], Number(jsFtvPower[i]) , Number(jsConPower[i]) ]);
            }
            
            // Set today chart options
            var optionsToday = {
	           	'width':1400,
               	'height':480,
              	'hAxis': {showTextEvery: parseInt(dataToday.getNumberOfRows() / 11)},
          	    //'backgroundColor' : '#ddd',
	    	    'legend' : { position: 'bottom' },
	    	    'connectSteps' : false,
	    	    'colors': ['orange', 'gray'],
            };
            
            // Instantiate and draw our chart, passing in some options.
            var chartToday = new google.visualization.AreaChart(document.getElementById('chartToday'));
            chartToday.draw(dataToday, optionsToday);
          }

		</script>
	</head>
	<body>
        <!--Divs that will hold the charts-->
        <table style="width:100%" bgcolor="#152934">
  			<tr>
  				<td><img src="logo.jpg" height="50" width="155"></td>
	    	</tr>
    	</table>
        <table style="width:100%">
  			<tr><td>&nbsp;</td></tr>
  			<tr><td>&nbsp;</td></tr>
  			<tr>
    			<td align="center"><font face="arial" size="5" color="#000000" ><b><%=completeDate%> Detailed Production and Comsumption</b></font></td>
	    	</tr>
	    	  			<tr><td> </td></tr>
  			<tr>
    			<td align="center"><font face="arial" size="2" color="#000000" ><b>
    				<script type="text/javascript">
        				document.write(ftvEnergyToday.toLocaleString())
      				</script>
	    			KWh produced - 
    				<script type="text/javascript">
        				document.write(conEnergyToday.toLocaleString())
      				</script>
	    			KWh consumed</b></font>
	    		</td>
	    	</tr>
  			<tr>
    			<td><div id="chartToday"></div></td>
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