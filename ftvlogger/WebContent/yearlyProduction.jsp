<%@ page import="java.sql.*, javax.sql.*, javax.naming.*, java.util.*" %>
<%@ page import="ftvlogger.RunDBStatement" %>
<html>
	<head>
		 <title>FTV logger</title>
		 <link rel="icon" href="favicon.ico" />
	<%

		RunDBStatement stm = new RunDBStatement();

		//Get Year Power Data
		String statement = "select substr(date,1,4),sum(ftv_energy),sum(con_energy) from henergy group by substr(date,1,4)";
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
            var dataYears = new google.visualization.DataTable();
            dataYears.addColumn('string', 'Year');
            dataYears.addColumn('number', 'Production (KWh)');        
            dataYears.addColumn('number', 'Consumption (KWh)');  

            for (var i = 0; i < jsDate.length; i++) {
    			dataYears.addRow([ jsDate[i], Number(jsFtvEnergy[i]) , Number(jsConEnergy[i]) ]);
            }
            
            // Set year chart options
            var optionsYears = {
	           	'width':1400,
               	'height':480,
	    	    'legend' : { position: 'bottom' },
	    	    'connectSteps' : false,
	    	    'colors': ['orange', 'gray'],
            };
            
            // Instantiate and draw our chart, passing in some options.
            var chartYears = new google.visualization.ColumnChart(document.getElementById('chartYears'));
            function selectHandler() {
                var selection = chartYears.getSelection()[0].row;
                var year = dataYears.getValue(selection,0);
                
    		 	var protocol = window.location.protocol;
    		 	var host = window.location.host;
    		 	var pathname = window.location.pathname;

    		 	window.open(protocol+'//'+host+'/'+pathname.split('/')[1]+'/monthlyProduction.jsp?year='+year,'_self');
            }
            google.visualization.events.addListener(chartYears, 'select', selectHandler);   
            chartYears.draw(dataYears, optionsYears);

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
    			<td align="center"><font face="arial" size="5" color="#000000" ><b>Yearly Production and Comsumption</b></font></td>
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
    			<td><div id="chartYears"></div></td>
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