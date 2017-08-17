<%@ page import="java.sql.*, javax.sql.*, javax.naming.*, java.util.*" %>
<%@ page import="ftvlogger.RunDBStatement" %>
<html>
	<head>
		 <title>FTV logger</title>
		 <link rel="icon" href="favicon.ico" />
	<%

		RunDBStatement stm = new RunDBStatement();
	
		String year = request.getParameter("year");


		//Get Year Power Data
		String statement = "select substr(date,6,2),sum(ftv_energy),sum(con_energy) from henergy where substr(date,1,4) = '"+year+"' group by substr(date,1,7)";
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
            var dataMonths = new google.visualization.DataTable();
            dataMonths.addColumn('string', 'Month');
            dataMonths.addColumn('number', 'Production (KWh)');        
            dataMonths.addColumn('number', 'Consumption (KWh)');  

            for (var i = 0; i < jsDate.length; i++) {
      			switch (jsDate[i]) {
     	        case "01":  monthCom = "Jan";
	  	        	break;
	  	        case "02":  monthCom = "Feb";
	  	        	break;
	  	        case "03":  monthCom = "Mar";
	  	        	break;
	  	       	case "04":  monthCom = "Apr";
	  	       		break;
	  	       	case "05":  monthCom = "May";
	  	       		break;
	  	       	case "06":  monthCom = "Jun";
	  	       		break;
	  	        case "07":  monthCom = "Jul";
	  	        	break;
	  	        case "08":  monthCom = "Aug";
	  	        	break;
	  	        case "09":  monthCom = "Sep";
	  	        	break;
	  	        case "10": monthCom = "Oct";
	  	        	break;
	  	        case "11": monthCom = "Nov";
	  	        	break;
	  	        case "12": monthCom = "Dec";
	  	        	break;
	  	        default: monthCom = "Invalid month";
	  	        	break;
      	        }
    			dataMonths.addRow([ monthCom, Number(jsFtvEnergy[i]) , Number(jsConEnergy[i]) ]);
            }
            
            // Set year chart options
            var optionsMonths = {
	           	'width':1400,
               	'height':480,
	    	    'legend' : { position: 'bottom' },
	    	    'connectSteps' : false,
	    	    'colors': ['orange', 'gray'],
            };
            
            // Instantiate and draw our chart, passing in some options.
            var chartMonths = new google.visualization.ColumnChart(document.getElementById('chartMonths'));
            function selectHandler() {
                var selection = chartMonths.getSelection()[0].row;
                var month = dataMonths.getValue(selection,0);
                
    		 	var protocol = window.location.protocol;
    		 	var host = window.location.host;
    		 	var pathname = window.location.pathname;

    		 	window.open(protocol+'//'+host+'/'+pathname.split('/')[1]+'/dailyProduction.jsp?month='+month+<%=year%>,'_self');
            }
            google.visualization.events.addListener(chartMonths, 'select', selectHandler);   
            chartMonths.draw(dataMonths, optionsMonths);

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
    			<td align="center"><font face="arial" size="5" color="#000000" ><b><%=year%> Production and Comsumption</b></font></td>
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
    			<td><div id="chartMonths"></div></td>
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