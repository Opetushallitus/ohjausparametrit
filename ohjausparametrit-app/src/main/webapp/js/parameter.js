

function ParameterListController($scope, $location, $routeParams, ParemeterModel) {
    $scope.model = ParemeterModel;

    HakuModel.init($routeParams.hakuOid);
    $scope.$watch('model.hakuOid', function() {
        if($scope.model.hakuOid && $scope.model.hakuOid.oid != $routeParams.hakuOid) {
            $location.path('/haku/' + HakuModel.hakuOid.oid + '/hakukohde/');
        }
    });

}
