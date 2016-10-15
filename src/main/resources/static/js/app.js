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
        min_length:{label:"Min length", inputType:"text", dataType:["integer"]},
        max_length:{label:"Max length", inputType:"text", dataType:["integer"]},
        exact_length:{label:"Exact length", inputType:"text", dataType:["integer"]},
        greater_than:{label:"Greater than", inputType:"text", dataType:["integer"]},
        less_than:{label:"Less than", inputType:"text", dataType:["integer"]},
        greater_than_equal_to:{label:"Greater than equal to", inputType:"text", dataType:["integer"]},
        less_than_equal_to:{label:"Less than equal to", inputType:"text", dataType:["integer"]},
        numeric:{label:"Number", inputType:"text", dataType:["integer"]},
        decimal:{label:"Decimal", inputType:"text", dataType:["integer"]},
        matches:{label:"Match", inputType:"text", dataType:["integer"]},
        valid_url:{label:"Valid URL", inputType:"text", dataType:["integer"]},
        valid_email:{label:"Vaild email", inputType:"text", dataType:["integer"]},
        regex_match:{label:"Regex match", inputType:"text", dataType:["integer"]},
        in_list:{label:"In list", inputType:"text", dataType:["integer"]},
        alpha:{label:"Alpha", inputType:"text", dataType:["integer"]}
    };





    
    $scope.addValidation = function (field) {

        field.validationErr = "";

        var vKey = $scope.options.validationRule;
        var vValue = $scope.options.validationRuleValue;


        if(typeof vKey == 'undefined' || typeof vValue == 'undefined' || vKey == "null"){
            field.validationErr = "Please select correct rule and value.";
            return;
        }

        //if already exists
        for(var i = 0; i < field.validations.length; i++){
            if(field.validations[i].key == vKey){
                field.validationErr = "This rule is already added.Please remove rule first to add again.";
                return;
                break;
            }
        }

        //datatype validation

        console.log($scope.options.rules[vKey]);
        for(var i = 0; i < $scope.options.rules[vKey].dataType.length; i++){
            if($scope.options.rules[vKey].dataType[i] == "integer" && !isInt(vValue)){
                field.validationErr = "Rule value must be integer.";
                return;
                break;
            }
        }



        var newValidation = {
            key: $scope.options.validationRule,
            value: $scope.options.validationRuleValue,
        }

        field.validations.push(newValidation);

        $scope.options.validationRule = "null";
        delete($scope.options.validationRuleValue);



    }




    $scope.fieldIsrequred = function (field) {

        if(field.is_required){
            field.validations.push({key: "required", value: true});
        } else{
            //remove validation
            $scope.removeValidationRule(field, "required");
        }

    }

    $scope.removeValidationRule = function (field, rule) {
        for(var i = 0; i < field.validations.length; i++){
            if(field.validations[i].key == rule){
                field.validations.splice(i, 1);
                break;
            }
        }
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


    $scope.updateForm = function () {

        var token = $("meta[name='_csrf']").attr("content");

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

function isInt(value) {
    return !isNaN(value) &&
        parseInt(Number(value)) == value &&
        !isNaN(parseInt(value, 10));
}