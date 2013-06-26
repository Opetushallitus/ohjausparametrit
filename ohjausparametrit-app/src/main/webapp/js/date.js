var app = angular.module('date', ['ui.directives']);

app.controller('MainCtrl', function($scope) {
    console.log("Date::MainCtrl...");

    $scope.name = 'World';
    $scope.date = new Date();

    console.log("Date::MainCtrl... done.");
});
