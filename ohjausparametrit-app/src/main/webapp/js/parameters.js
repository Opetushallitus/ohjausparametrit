
angular.module('parameters', ['ui.bootstrap', "firebase"]).
        config(function($routeProvider) {
    $routeProvider.
            when('/WASSLASH', {controller: ListCtrl, templateUrl: 'list.html'}).
            when('/', {controller: Test2Ctrl, templateUrl: 'parameters_list.html'}).
            when('/edit/:projectId', {controller: EditCtrl, templateUrl: 'details.html'}).
            when('/new', {controller: CreateCtrl, templateUrl: 'details.html'}).
            otherwise({redirectTo: '/'});
});

function Test2Ctrl($scope, $http, angularFire) {
    console.info("Test2Ctrl()");

    $scope.date = new Date();
    $scope.opened = true;


    $scope.parameters = [];
    $scope.selectedParameter = null;
    $scope.selectedParameterValue = null;

    $scope.parameterTypes = [
        {name: "Kyllä/Ei", type: "BOOLEAN"},
        {name: "Päivämäärä", type: "DATE"},
        {name: "Päivämääräväli", type: "DATE_RANGE"},
        {name: "Kokonaisluku", type: "INTEGER"},
    ];

    $http.get('parameters.json').success(function(data) {
        console.info("parameters load success!");
        $scope.parameters = data;
        // $scope.selectedParameter = $scope.parameters[0];

        ref.push($scope.parameters);
    });

    $scope.doCreateNewParameter = function() {
        console.info("doCreateNewParameter()");

        $scope.selectedParameter = {
            "path": "uusi.parametri.polku",
            "name": "uuden parametrin nimi",
            "description": {
                "kieli_fi": "Kuvaus suomeksi",
                "kieli_en": "Description in english",
                "kieli_sv": "Vad är detta här parameter?"
            },
            "values": [],
            "type": "INTEGER"
        };

        $scope.parameters.push($scope.selectedParameter);
        $scope.selectedParameterValue = null;
    };

    $scope.doSelectParameter = function(p) {
        console.info("doSelectParameter() " + p.path);

        if ($scope.selectedParameter == p) {
            $scope.selectedParameter = null;
        } else {
            $scope.selectedParameter = p;
        }
        $scope.selectedParameterValue = null;
    };

    $scope.doSelectParameterValue = function(pv) {
        console.info("doSelectParameterValue() " + pv.target);

        if ($scope.selectedParameterValue == pv) {
            $scope.selectedParameterValue = null;
        } else {
            $scope.selectedParameterValue = pv;
        }
    };

}


function ListCtrl($scope, $http) {
    console.info("ListCtrl()");
    // $scope.parameters = [];

    $scope.selectedParameter = "LOADING...";

    $http.get('parameters.json').success(function(data) {
        console.info("parameters load success!");
        $scope.parameters = data;
        $scope.selectedParameter = $scope.parameters[0];
    });

    // $scope.selectedParameterId = 'NONE';
}

function CreateCtrl($scope, $location, $timeout, $http) {
    console.info("CreateCtrl()");

//    $scope.save = function() {
//        Projects.add($scope.project, function() {
//            $timeout(function() {
//                $location.path('/');
//            });
//        });
//    }
}

function EditCtrl($scope, $location, $routeParams, $dialog) {
    console.info("EditCtrl(): " + $routeParams.projectId);
    console.info("  scope: " + $scope);

    $scope.selectedParameterPath = $routeParams.projectId;
    // $scope.selectedParameter = findParameterByPath($scope, $scope.selectedParameterPath);

//    var d = $dialog.dialog({
//        backdrop: true,
//        keyboard: true,
//        backdropClick: true,
//        templateUrl: "TEST",
//        controller: "DialogCtl"
//    });
//
//    d.open().then(function(result) {
//        console.log("d.open().then: " + result);
//        $location.path("/");
//    });
}


function DialogCtl($scope, $location) {
    console.info("DialogCtl()");
    $location.path("/");
}


function findParameterByPath(scope, parameterPath) {
    console.info("findParameterByPath()");

    for (var i = 0; i < scope.parameters.length; i++) {
        if (scope.parameters[i].path == parameterPath) {
            return scope.parameters[i];
        }
    }
}
