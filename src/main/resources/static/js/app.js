$.fn.exists = function(callback) {
    var args = [].slice.call(arguments, 1);

    if (this.length) {
        callback.call(this, args);
    }
    return this;
};


$('.datatable').exists(function() {
    $(".datatable").DataTable();
});

$('#menu-toggle').exists(function() {
$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});
});


var app = angular.module('dynForms', []);


app.controller('createForm', function($scope) {

    $scope.form = {title:"Edit title here.", description:"Description..."};
    $scope.form.fields = [];
    $scope.addField = {};
    $scope.addField.lastAddedID = 0;





});



app.directive('fieldDirective', function($http, $compile) {


    var getTemplateUrl = function(field) {
        return 'forms/design/'+field.type+'.html';
    }

    var linker = function(scope, element, attrs) {
        // GET template content from path

        var templateUrl = getTemplateUrl(scope.field);
        $http.get(templateUrl).success(function(data) {
            element.html(data);
            $compile(element.contents())(scope);
        });
    }

    return {
        restrict: "EA",
        scope: false,
        link: linker,
    };

});
