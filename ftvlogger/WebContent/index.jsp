<%@ page import="java.sql.*, javax.sql.*, javax.naming.*, java.util.*" %>
<%@ page import="ftvlogger.RunDBStatement" %>
<html>
	<head>
	
		<meta http-equiv="refresh" content="60">
	
		 <title>FTV logger</title>
		 <link rel="icon" href="favicon.ico" />
		 
 	<%
	 	response.setIntHeader("Refresh", 60);
 		
 		RunDBStatement stm = new RunDBStatement();
    	
    	//Get Today Power Data
  		String statement = "select date,ftv_power,ftv_energy,con_power,con_energy from rs485data where date > curdate()";
  		List<String> rs = stm.getResultSet("jdbc/ftvdb", statement);    
		StringBuilder sbDate = stm.getStringBuilder(rs, 0, "date");
		StringBuilder sbFtvPower = stm.getStringBuilder(rs, 1, "measure");
		StringBuilder sbFtvEnergy = stm.getStringBuilder(rs, 2, "measure");
		StringBuilder sbConPower = stm.getStringBuilder(rs, 3, "measure");
		StringBuilder sbConEnergy = stm.getStringBuilder(rs, 4, "measure");
	  	
    	//Get Yesterday Power Data
  		statement = "select date,ftv_power,ftv_energy,con_power,con_energy from rs485data where date > curdate()-1 and date < curdate()";
  		List<String> rsY = stm.getResultSet("jdbc/ftvdb", statement);    
		StringBuilder sbDateY = stm.getStringBuilder(rsY, 0, "date");
		StringBuilder sbFtvPowerY = stm.getStringBuilder(rsY, 1, "measure");
		StringBuilder sbFtvEnergyY = stm.getStringBuilder(rsY, 2, "measure");
		StringBuilder sbConPowerY = stm.getStringBuilder(rsY, 3, "measure");
		StringBuilder sbConEnergyY = stm.getStringBuilder(rsY, 4, "measure");

    	//Get Last 10 Days Energy Data
  		statement = "select date,ftv_energy,con_energy from henergy where date > curdate()-11";
  		List<String> rsL10 = stm.getResultSet("jdbc/ftvdb", statement); 
		StringBuilder sbL10Day = stm.getStringBuilder(rsL10, 0, "shortDate1");
		StringBuilder sbL10FtvEn = stm.getStringBuilder(rsL10, 1, "measure");
		StringBuilder sbL10ConEn = stm.getStringBuilder(rsL10, 2, "measure");
 
	%>

        <!--Load the AJAX API-->
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
		<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript">

          // Load the Visualization API and the core chart package.
          google.load('visualization', '1.0', {'packages':['corechart']});
		  google.load('visualization', '1.0', {'packages':['gauge']});

          // Set a callback to run when the Google Visualization API is loaded.
          google.setOnLoadCallback(drawChart);

          // Callback that creates and populates a data table,
          // instantiates the pie chart, passes in the data and
          // draws it.
          function drawChart() {

            // Create the today data table.
            var dataToday = new google.visualization.DataTable();
            dataToday.addColumn('string', 'Data');
            dataToday.addColumn('number', 'Production (W)');        
            dataToday.addColumn('number', 'Consumption (W)');  

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
            
            for (var i = 0; i < jsDate.length; i++) {
    			dataToday.addRow([ jsDate[i], Number(jsFtvPower[i]) , Number(jsConPower[i]) ]);
            }
            
            ftvEnergyToday = Number(jsFtvEnergy[jsDate.length-1]) - Number(jsFtvEnergy[0]);
            conEnergyToday = Number(jsConEnergy[jsDate.length-1]) - Number(jsConEnergy[0]);
            
            // Create the yesterday data table.
            var dataYesterday = new google.visualization.DataTable();
            dataYesterday.addColumn('string', 'Data');
            dataYesterday.addColumn('number', 'Production (W)');        
            dataYesterday.addColumn('number', 'Consumption (W)');  

            tempDateY="<%=sbDateY.toString()%>";
            var jsDateY = new Array();
            jsDateY = tempDateY.split(',','<%=rsY.size()%>');
            
            tempFtvPowerY="<%=sbFtvPowerY.toString()%>";
            var jsFtvPowerY = new Array();
            jsFtvPowerY = tempFtvPowerY.split(',','<%=rsY.size()%>');
            
            tempFtvEnergyY="<%=sbFtvEnergyY.toString()%>";
            var jsFtvEnergyY = new Array();
            jsFtvEnergyY = tempFtvEnergyY.split(',','<%=rsY.size()%>');

            tempConPowerY="<%=sbConPowerY.toString()%>";
            var jsConPowerY = new Array();
            jsConPowerY = tempConPowerY.split(',','<%=rsY.size()%>');

            tempConEnergyY="<%=sbConEnergyY.toString()%>";
            var jsConEnergyY = new Array();
            jsConEnergyY = tempConEnergyY.split(',','<%=rsY.size()%>');

            for (var i = 0; i < jsDateY.length; i++) {
            	dataYesterday.addRow([ jsDateY[i], Number(jsFtvPowerY[i]) , Number(jsConPowerY[i]) ]);
            }
 
            ftvEnergyYesterday = Number(jsFtvEnergyY[jsDateY.length-1]) - Number(jsFtvEnergyY[0]);
            conEnergyYesterday = Number(jsConEnergyY[jsDateY.length-1]) - Number(jsConEnergyY[0]);

            //Create Instanteneous Power Data Table
            var dataInst = google.visualization.arrayToDataTable([
	          ['Label', 'Value'],
    	      ['Prod', Number(jsFtvPower[jsDate.length-1])],
        	  ['Cons', Number(jsConPower[jsDate.length-1])],
        	]);
            
            
         // Create the L10 data table.
            var dataL10 = new google.visualization.DataTable();
            dataL10.addColumn('string', 'Day');
            dataL10.addColumn('number', 'Production (KWh)');        
            dataL10.addColumn('number', 'Consumption (KWh)');  

            tempL10Day="<%=sbL10Day.toString()%>";
            var jsL10Day = new Array();
            jsL10Day = tempL10Day.split(',','<%=rsL10.size()%>');
            
            tempL10FtvEn="<%=sbL10FtvEn.toString()%>";
            var jsL10FtvEn = new Array();
            jsL10FtvEn = tempL10FtvEn.split(',','<%=rsL10.size()%>');
            
            tempL10ConEn="<%=sbL10ConEn.toString()%>";
            var jsL10ConEn = new Array();
            jsL10ConEn = tempL10ConEn.split(',','<%=rsL10.size()%>');

            for (var i = 0; i < jsL10Day.length; i++) {
            	dataL10.addRow([ jsL10Day[i], Number(jsL10FtvEn[i]) , Number(jsL10ConEn[i]) ]);
            }
 
            // Set today chart options
            var optionsToday = {'title':'Today ( '+ftvEnergyToday+' KWh produced - '+conEnergyToday+' KWh consumed )',
	           	'width':1000,
               	'height':290,
              	'hAxis': {showTextEvery: parseInt(dataToday.getNumberOfRows() / 11)},
          	    //'backgroundColor' : '#ddd',
	    	    'legend' : { position: 'bottom' },
	    	    'connectSteps' : false,
	    	    'colors': ['orange', 'gray'],
            };
            
            // Set yesterday chart options
            var optionsYesterday = {'title':'Yesterday ( '+ftvEnergyYesterday+' KWh produced - '+conEnergyYesterday+' KWh consumed )',
	           	'width':680,
               	'height':200,
              	'hAxis': {showTextEvery: parseInt(dataYesterday.getNumberOfRows() / 11)},
	    	    'legend' : { position: 'bottom' },
	    	    'connectSteps' : false,
	    	    'colors': ['orange', 'gray'],
            };
            
            // Set Instanteneous Power chart options
			var optionsInst = {
		    	width: 600, height: 150,
		    	redFrom: 2001, redTo: 3000,
		    	yellowFrom:1001, yellowTo: 2000,
		    	greenFrom: 0, greenTo: 1000,
		        minorTicks: 5, max: 3000
		    };
            
            // Set L10 chart options
            var optionsL10 = {'title':'Last 10 days Production (KWh)',
	           	'width':820,
               	'height':280,
	    	    'legend' : { position: 'bottom' },
	    	    'connectSteps' : false,
	    	    'colors': ['orange', 'gray'],
            };
             
            // Instantiate and draw our chart, passing in some options.
            var chartToday = new google.visualization.AreaChart(document.getElementById('chartToday'));
            chartToday.draw(dataToday, optionsToday);
            var chartYesterday = new google.visualization.AreaChart(document.getElementById('chartYesterday'));
            chartYesterday.draw(dataYesterday, optionsYesterday);
            var chartInst = new google.visualization.Gauge(document.getElementById('chartInst'));
            chartInst.draw(dataInst, optionsInst);
            var chartL10 = new google.visualization.ColumnChart(document.getElementById('chartL10'));
            function selectHandler() {
                var selection = chartL10.getSelection()[0].row;
                var dataVal = dataL10.getValue(selection,0);
    		 	dataValP=dataVal.split(' ');
                
    		 	var protocol = window.location.protocol;
    		 	var host = window.location.host;
    		 	var pathname = window.location.pathname;

    		 	var yearNow=new Date().getFullYear();
    		 	var monthNow=new Date().getMonth();
    		 	//decrease year we are in january and we're requesting a December report
				if(dataValP[1] == 'Dec' && monthNow == 0){
					year=parseInt(yearNow)-1;
				}else{
					year=yearNow;
				}
    		 	window.open(protocol+'//'+host+'/'+pathname.split('/')[1]+'/detailedDailyProduction.jsp?date='+dataValP[0]+dataValP[1]+year,'_blank');
            }
            google.visualization.events.addListener(chartL10, 'select', selectHandler);   
            chartL10.draw(dataL10, optionsL10);

          }

        //get last hour
		lsDate="<%=sbDate.toString()%>";
        var jsLsDate = new Array();
        jsLsDate = lsDate.split(',','<%=rs.size()%>');
        var lastdate = jsLsDate[jsLsDate.length-1];

        </script>
      </head>

	<body>
        <!--Divs that will hold the charts-->
        <table style="width:100%" bgcolor="#152934">
  			<tr>
  				<td><img src="logo.jpg" height="50" width="155"></td>
    			<td align="right">
					<a href="yearlyProduction.jsp" style="text-decoration:none"><font face="arial" size="3" color="#FFFFFF" >Historical Data</font></a>&nbsp;&nbsp;
				</td>
	    	</tr>
    	</table>
        <table style="width:100%">
  			<tr>
    			<td><div id="chartToday"></div></td>
    			<td>
    				<table>
    					<tr>
    						<td><b>&nbsp;&nbsp;Last Power Rating Detected @<script type="text/javascript">document.write(lastdate);</script> (W)</b></td>
    					</tr>
    					<tr>
    						<td><div id="chartInst"></div></td>
    					</tr>
    				</table>
    				
    			</td>
	    	</tr>
    	</table>
        <table style="width:100%">
  			<tr>
    			<td style="vertical-align:bottom;"><div id="chartYesterday"></div></td>
    			<td style="vertical-align:bottom;"><div id="chartL10"></div></td>
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
	</body>
</html>
