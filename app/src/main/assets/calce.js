//Модель
var model = {
    payments: [{ month: '0 month', amount: '$0', perc: '$0', main: '$0', pay: '$0', passed: false, style: 'well', font: 'inherit' }]
};

//Модуль

var myApp = angular.module("App", ['AngularPrint'], function ($locationProvider) {
    $locationProvider.html5Mode(true);
});

//var myApp = angular.module("App", ['AngularPrint', '...']);
//Контоллер      
myApp.controller("CourceListCtrl", CourceList);

function CourceList($scope) {
    $scope.data = model;
    $scope.paymentMonth = 'Monthly payments:';


    var am = parseFloat(localStorage.loan_amount);
    var ap = parseFloat(localStorage.loan_apr);
    var ye = parseFloat(localStorage.loan_years);
    var co = parseFloat(localStorage.loan_commis);
    var an = (localStorage.loan_annu);
    if (isNaN(am)) {
        am = 10000;
    }
    if (isNaN(ap)) {
        ap = 10;
    }
    if (isNaN(ye)) {
        ye = 2;
    }
    if (isNaN(co)) {
        co = 0;
    }
    if (!isNaN(an)) {
        if (an == 1)
            $scope.payment = {
                type: 'Annu'
            };
        else {
            $scope.payment = {
                type: 'Diff'
            };
        }
    }
    else {
        $scope.payment = {
            type: 'Annu'
        };
    }
    $scope.amount = am;
    $scope.apr = ap;
    $scope.years = ye;
    $scope.commis = co;

    $scope.amountText = $scope.amount.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');

    //обработчик нажатия по кнопке "Печать"          
   

    $scope.clear = function () {
        $scope.data.payments.length = 0; // clear array
        $scope.amountText = $scope.amount.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
        $scope.monthly = '';
        $scope.percent = '';
        $scope.total = '';
        $scope.count = '';      
        $scope.fee = 0;
        $scope.lastMonth = '';
        $scope.lastmonthly = '';
        $scope.result = false;
        $scope.logo = false;
    }

    //обработчик нажатия по кнопке "Расчитать"  
    $scope.calculateByMonth = function () {
        $scope.result = true;
        $scope.logo = true;
        var setGraph = [['1996', 300, 162, 242]];
        var an = 0;
        var fee = 0;
        if ($scope.payment.type == 'Annu') {
            an = 1;
        }
        save($scope.amount, $scope.apr, $scope.years, $scope.commis, an);
        $scope.commisShow = $scope.commis > 0;
        $scope.data.payments.length = 0; // clear array
        setGraph.length = 0;// clear array
        $scope.count = $scope.years * 12;
        fee = $scope.years * 12 * $scope.commis;
        if ($scope.commis > 0) {
            $scope.fee = fee.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');;
        }
        else {
            $scope.fee = 0;
        }
       

        if ($scope.payment.type == 'Annu') { //Аннуитетные-------------------------      
            var interest = parseFloat($scope.apr / 100);
            var percTotal = 0;
            var mainTotal = 0;
            var monthlyTotal = 0;
            $scope.paymentMonth = 'Monthly payments:';
            $scope.lastMonth = '';
            $scope.lastmonthly = '';
            $scope.lasttr = false;        

            var amountRest = $scope.amount; //остаток основного долга
            var percent = parseFloat(amountRest * interest / 12);
            var monthly = ($scope.amount * (interest / 12)) / (1 - (1 / Math.pow(1 + (interest / 12), ($scope.years * 12)))) + $scope.commis; // fixed
            $scope.monthly = monthly.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
            var mainloan = monthly - percent - $scope.commis;

            for (i = 1; i < ($scope.years * 12) + 1; i++) {
                var mystyle = 'success';
                if (i % 2 == 0) {
                    mystyle = 'success';
                }
                else {
                    mystyle = 'warning';
                }
                $scope.data.payments.push({
                    month: i + ' month',
                    amount: amountRest.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                    perc: (percent + $scope.commis).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                    main: mainloan.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                    pay: monthly.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                    passed: false,
                    style: mystyle,
                    font: 'inherit'
                });

                setGraph.push([
                    i + '',
                    monthly,
                    mainloan,
                    percent + $scope.commis
                    ]);

                amountRest -= mainloan;
                mainTotal += mainloan;
                percTotal += percent + $scope.commis;
                monthlyTotal += monthly;

                percent = parseFloat(amountRest * interest / 12);
                mainloan = monthly - percent - $scope.commis;
            }
            //Итого:
            $scope.data.payments.push({
                month: "Total:",
                amount: "0",
                perc: percTotal.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                main: mainTotal.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                pay: monthlyTotal.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                passed: true,
                style: 'danger',
                font: 'large'
            });
            $scope.total = monthlyTotal.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
            $scope.percent = (percTotal - fee).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
            $scope.feepercent = percTotal.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
          
            diagram(percTotal, $scope.amount, $scope.commis * $scope.years * 12, 'Total payments: $' + $scope.total);
            var titlegraph = 'Even Total Payment Schedule';   
            graph(setGraph, titlegraph);
        }
        else { //Дифференцированные платежи           --------------------------------------------------------------------------------------        
            var interest = parseFloat($scope.apr / 100);
            var percTotal = 0;
            var mainTotal = 0;
            var monthlyTotal = 0;           
            $scope.lasttr = true;
            $scope.paymentMonth = 'Payment in first month:';
            $scope.lastMonth = 'Payment in last month:';

            var amountRest = $scope.amount; //остаток основного долга            
            var mainloan = parseFloat($scope.amount / ($scope.years * 12)); //fixed
            var percent = parseFloat(amountRest * interest / 12);
            var monthly = mainloan + percent + $scope.commis;
            var lastm = 0;

            $scope.monthly = monthly.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');            

            for (i = 1; i < ($scope.years * 12) + 1; i++) {
                var mystyle = 'success';
                if (i % 2 == 0) {
                    mystyle = 'success';
                }
                else {
                    mystyle = 'warning';
                }
                $scope.data.payments.push({
                    month: i + ' month',
                    amount: amountRest.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                    perc: (percent + $scope.commis).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                    main: mainloan.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                    pay: monthly.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                    passed: false,
                    style: mystyle,
                    font: 'inherit'
                });

                setGraph.push([
                   i + '',
                   monthly,
                   mainloan,
                   percent + $scope.commis]);
                amountRest -= mainloan;
                mainTotal += mainloan;
                percTotal += percent + $scope.commis;
                monthlyTotal += monthly;
                lastm = monthly;
                percent = parseFloat(($scope.amount - (i * ($scope.amount / ($scope.years * 12)))) * (interest / 12));
                monthly = mainloan + percent + $scope.commis;
            }
            $scope.lastmonthly = lastm.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
            //Итого:
            $scope.data.payments.push({
                month: "Total:",
                amount: "0",
                perc: percTotal.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                main: mainTotal.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                pay: monthlyTotal.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,'),
                passed: true,
                style: 'danger',
                font: 'large'
            });
            $scope.total = monthlyTotal.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
            $scope.percent = (percTotal - fee).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
            $scope.feepercent = percTotal.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
                    
            diagram(percTotal, $scope.amount, $scope.commis * $scope.years * 12, 'Total payments: $' + $scope.total);
            var titlegraph = 'Even Principal Payment Schedule';     
            graph(setGraph, titlegraph);
        }
    }

    $scope.showText = function (passed) {
        return passed ? "Yes" : "No";
    }
    $scope.setStyle = function (passed) {
        return passed ? "color:green" : "color:red; font-weight: bold";
    }
    $scope.newPaymentTypeValue = function (value) {
        $scope.log = value;
    }
}

function save(amount, apr, years, commis, annu) {
    if (window.localStorage) {  // Only do this if the browser supports it
        localStorage.loan_amount = amount;
        localStorage.loan_apr = apr;
        localStorage.loan_years = years;
        localStorage.loan_commis = commis;
        localStorage.loan_annu = annu;
    }
}

function diagram(percTotal, amount, fee, title) {
    document.getElementById("diagram").innerHTML = "";
    amount = Math.round(amount);
    var perc = Math.round(percTotal - fee);
    fee = Math.round(fee)
    if (fee == 0) {
        chart = anychart.pie3d([
                                    ['Total Principal Paid', amount],
                                    ['Total Interest Paid', perc]
        ]);
    }
    else {
        chart = anychart.pie3d([
                                    ['Total Principal Paid', amount],
                                    ['Total Interest Paid', perc],
                                    ['Total Maintenance Fee', fee]
        ]);
    }
    

    // set container id for the chart
    chart.container('diagram');

    // set chart title text settings
    chart.title(title);

    chart.legend().position('right');
    chart.legend().itemsLayout('vertical');
    chart.legend().align('right');
    //set chart radius
    chart.radius('100%');

    // create empty area in pie chart
    chart.innerRadius('30%');

    //chart.labels().position('outside');

    // initiate chart drawing
    chart.draw();
}

function graph(set, title) {
    var gr = document.getElementById("graph");
    gr.innerHTML = "";
    gr.setAttribute("style", "display:block;height:400px");    
    var dataSet = anychart.data.set(set);

    //--------------------------------------------
    // map data for the first series, take x from the zero column and value from the first column of data set
    var seriesData_1 = dataSet.mapAs({ x: [0], value: [1] });

    // map data for the second series, take x from the zero column and value from the second column of data set
    var seriesData_2 = dataSet.mapAs({ x: [0], value: [2] });

    // map data for the third series, take x from the zero column and value from the third column of data set
    var seriesData_3 = dataSet.mapAs({ x: [0], value: [3] });

    // create line chart
    chart = anychart.line();

    // turn on chart animation
    chart.animation(true);

    // turn on the crosshair
    chart.crosshair().enabled(true).yLabel().enabled(false);
    chart.crosshair().yStroke(null);

    // set tooltip mode to point
    chart.tooltip().positionMode('point');

    // set chart title text settings
    chart.title(title);
    chart.title().padding([0, 0, 5, 0]);

    // set yAxis title
    chart.yAxis().title('Payments in Dollars');
    chart.xAxis().labels().padding([5]);

    // create first series with mapped data
    var series_1 = chart.line(seriesData_1);
    series_1.name('Payment =');
    series_1.hoverMarkers().enabled(true).type('circle').size(4);
    series_1.tooltip().position('right').anchor('left').offsetX(5).offsetY(5);

    // create second series with mapped data
    var series_2 = chart.line(seriesData_2);
    series_2.name('Principal');
    series_2.hoverMarkers().enabled(true).type('circle').size(4);
    series_2.tooltip().position('right').anchor('left').offsetX(5).offsetY(5);

    // create third series with mapped data
    var series_3 = chart.line(seriesData_3);
    series_3.name('+ Interest');
    series_3.hoverMarkers().enabled(true).type('circle').size(4);
    series_3.tooltip().position('right').anchor('left').offsetX(5).offsetY(5);

    // turn the legend on
    chart.legend().enabled(true).fontSize(13).padding([0, 0, 10, 0]);

    // set container id for the chart and set up paddings
    chart.container('graph');
    chart.padding([10, 20, 5, 20]);

    // initiate chart drawing
    chart.draw();  
}
