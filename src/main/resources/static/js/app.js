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


app.controller('createForm', function($scope, $http) {

    var formId = $("meta[name='_form_id']").attr("content");

    $scope.options = {};

    $http({
        method: 'GET',
        url: 'http://localhost:8080/api/forms/'+formId,
    }).then(function successCallback(response) {
        $scope.form = response.data;
        $scope.options.lastAddedID = response.data.fields.length;
    }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
    });


    $scope.options.rules = {
        min_length:{inputType:"text", dataType:["integer"]},
        max_length:{inputType:"text", dataType:["integer"]}
    };
    
    $scope.addValidation = function (field) {

        var newValidation = {
            key: $scope.options.validationRule,
            value: $scope.options.validationRuleValue,
        }

        field.validations.push(newValidation);
        
    }




    //Add field
    $scope.addField = function (fieldType) {

       $scope.options.lastAddedID++;

        var newField = {
            "fieldId" : $scope.options.lastAddedID,
            "type" : fieldType,
            "name" : createIdentifier(),
            "label" : "New field - " + $scope.options.lastAddedID
        };

        if(fieldType == "multiple" || fieldType == "check" || fieldType == "select"){
            newField.options = [{id:1,title:"Option 1", pos:"1"}, {id:2,title:"Option 2", pos:"2"}, {id:3,title:"Option 3", pos:"3"}];
        }

        $scope.form.fields.push(newField);


        var token = $("meta[name='_csrf']").attr("content");


        console.log(token);


        $http({
            url: 'http://localhost:8080/api/forms',
            dataType: 'json',
            method: 'POST',
            data: $scope.form,
            headers: {
                "Content-Type": "application/json",
                'X-CSRF-TOKEN': token
            }
        }).success(function(response){
            console.log(response)
            $scope.response = response;
        }).error(function(error){
            console.log(error)
            $scope.error = error;
        });







    }


    // $scope.save = function () {
    //     $scope.story.$save(function (story, headers) {
    //         toastr.success("Submitted New Story");
    //         $location.path('/');
    //     });
    // }





});



app.directive('fieldDirective', function($http, $compile) {


    console.log("ddd");


    var getTemplateUrl = function(field) {
        return '/designer/'+field.type+'.html';
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


function createIdentifier() {
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < 10; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text.toLowerCase();
}