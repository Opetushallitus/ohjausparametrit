var app = angular.module('xstrap', ['$strap.directives']);

app.controller('MainCtrl', function($scope, $http, $modal) {

    $scope.datepicker = {date: new Date("2012-09-01T00:00:00.000Z")};

    $scope.checkbox = {
        "left": false,
        "middle": true,
        "right": false
    };

    $scope.modal = {content: 'Hello Modal', saved: false};
    $scope.viaService = function() {
        // do something
        var modal = $modal({
            template: 'modal.html',
            show: true,
            backdrop: 'static',
            scope: $scope
        });
    }
    $scope.parentController = function(dismiss) {
        console.warn(arguments);
        // do something
        dismiss();
    }


    $http.get('hakus.json').success(function(data) {
        console.info("haku load success!");
        $scope.hakus = data;
    });

    $http.get('parameters.json').success(function(data) {
        console.info("parameters load success!");
        $scope.parameters = data;
    });

    $scope.selectedHakuOid = '';

});

// "date-format"
app.directive('xdateFormat', function() {
    return {
        require: 'ngModel',
        link: function(scope, element, attr, ngModelCtrl) {
            ngModelCtrl.$formatters.unshift(function(valueFromModel) {
                r = new Date(valueFromModel);
                console.info("from model: " + valueFromModel + " --> " + (isNaN(r) ? "NAN" : r.toDateString()));
                return r;
            });

            ngModelCtrl.$parsers.push(function(valueFromInput) {
                if (valueFromInput === undefined) {
                    return ;
                }

                r = NaN;
                if (!isNaN(valueFromInput)) {
                  r = valueFromInput.getTime();
                }
                console.info("to model: " + valueFromInput + " --> " + r);
                return r;
            });
        }
    };
});


//app.value('$strapConfig', {
//  datepicker: {
//    language: 'fi'
//  }
//});
