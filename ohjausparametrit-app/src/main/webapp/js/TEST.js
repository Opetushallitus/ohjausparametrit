
var app = angular.module('TEST', []);

app.directive('datepicker', function($parse) {
    var directiveDefinitionObject = {
        restrict: 'A',
        link: function postLink(scope, iElement, iAttrs) {
            iElement.datepicker({
                dateFormat: 'dd.mm.yy',
                onSelect: function(dateText, inst) {
                    scope.$apply(function(scope) {
                        $parse(iAttrs.ngModel).assign(scope, dateText);
                    });
                }
            });
        }
    };

    return directiveDefinitionObject;
});



app.controller('HakuListCtrl', function($scope, $http) {

    console.info("HakuListCtrl()...");

    $http.get('hakus.json').success(function(data) {
        console.info("haku load success!");
        $scope.hakus = data;
    });

    $http.get('parameters.json').success(function(data) {
        console.info("parameters load success!");
        $scope.parameters = data;
    });

    $scope.selectedHakuOid = 'NONE';

    // Date stuff
    // scope.v.Dt = Date.parse(scope.v.Dt);

    console.info("HakuListCtrl()... done.");

});


//
//function HakuListCtrl($scope, $http) {
//
//    console.info("HakuListCtrl()...");
//
////    $scope.hakus = [
////    {"name": "Valitse haku",
////        "oid": ""},
////    {"name": "Yhteishaku 1",
////        "oid": "1.2.3.4.1"},
////    {"name": "Yhteishaku 2",
////        "oid": "1.2.3.4.2"},
////    {"name": "Yhteishaku 3",
////        "oid": "1.2.3.4.3"},
////    {"name": "Yhteishaku 4",
////        "oid": "1.2.3.4.4"},
////    {"name": "Yhteishaku 5",
////        "oid": "1.2.3.4.5"},
////];
//
//    $http.get('hakus.json').success(function(data) {
//        console.info("haku load success!");
//        $scope.hakus = data;
//    });
//
//    $http.get('parameters.json').success(function(data) {
//        console.info("parameters load success!");
//        $scope.parameters = data;
//    });
//
//    $scope.selectedHakuOid = '';
//
//    console.info("HakuListCtrl()... done.");
//}
