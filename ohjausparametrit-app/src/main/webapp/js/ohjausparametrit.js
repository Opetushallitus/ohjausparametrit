var app = angular.module('ohjausparametrit', ['ngResource', 'loading']);

var SERVICE_URL_BASE = SERVICE_URL_BASE || "";
var TEMPLATE_URL_BASE = TEMPLATE_URL_BASE || "";

//
// ROUTES
//
app.config(['$routeProvider', function($routeProvider) {
  $routeProvider.
      when('/parameters', {templateUrl: TEMPLATE_URL_BASE + 'parameters.html',   controller: ParameterListController}).
      when('/parameter/:parameterId', {templateUrl: TEMPLATE_URL_BASE + 'parameter-detail.html', controller: ParameterDetailController}).
      otherwise({redirectTo: '/parameters'});
}]);


//
// RESOURCES
//
app.factory('Parameter', function($resource) {
  return $resource(SERVICE_URL_BASE + "parametri", {}, {
    get: {method: "GET", isArray: true}
  });
});

