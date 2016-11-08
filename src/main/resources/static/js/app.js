$.fn.exists = function(callback) {
    var args = [].slice.call(arguments, 1);

    if (this.length) {
        callback.call(this, args);
    }
    return this;
};

$('[data-toggle="tooltip"]').exists(function() {
 $('[data-toggle="tooltip"]').tooltip()
});


$('.datatable').exists(function() {
    $(".datatable").DataTable();
});

$('#menu-toggle').exists(function() {
$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});
});



//select 2
$(".projectPicker").select2({
    theme: "bootstrap",
    ajax: {
        url: "http://localhost:8080/api/projects",
        dataType: 'json',
        delay: 250,
        data: function (params) {
            return {
                scol: 2,
                sqry: params.term,
                page: params.page
            };
        },
        processResults: function (result, params) {
            // parse the results into the format expected by Select2
            // since we are using custom formatting functions we do not need to
            // alter the remote JSON data, except to indicate that infinite
            // scrolling can be used
            params.page = params.page || 1;
            return {
                results: result.data,
                pagination: {
                    more: (params.page * 30) < result.recordsTotal
                }
            };
        },
        cache: false
    },
    escapeMarkup: function (markup) { return markup; }, // let our custom formatter work
    minimumInputLength: 1,
    templateResult: formatProject, // omitted for brevity, see the source of this page
    templateSelection: formatProjectSelection, // omitted for brevity, see the source of this page
    placeholder: "Enter name"
});



function formatProject (project) {
    if (project.loading) return "searching...";
    return  project.name;
}

function formatProjectSelection (project) {
    return project.name || project.id;
}

var app = angular.module('dynForms', ['ui.sortable']);


app.controller('projects', function($scope, $http) {


    $('#projectList').DataTable( {
        "processing": true,
        "serverSide": true,
        "searching": false,
        ajax: {
            "url": "http://localhost:8080/api/projects/datatable",
            "data": function(data) {
                planify(data);
            }
        },
        "columns": [
            {
                data: 'name',
                render: function (data ,type, full, meta) {
                    return '<a href="/project/'+full.id+'">'+data+'</a>';
                }
            },
            {
                data: 'description',
                render: function (data) {
                    return data.substr( 0, 35 );
                }
            },
            { data: 'formCount' },
            { data: 'owner' },
            { data: 'createdAt',
                render: function (data) {
                    return moment(data).format("MMMM Do YYYY, h:mm a");
                }
            }
        ]
    } );

    function planify(data) {
        for (var i = 0; i < data.columns.length; i++) {
            column = data.columns[i];
            column.searchRegex = column.search.regex;
            column.searchValue = column.search.value;
            delete(column.search);
        }
    }




    $scope.project = {};
    $scope.createProject = function () {

        var token = $("meta[name='_csrf']").attr("content");

        $http({
            url: 'http://localhost:8080/api/projects',
            dataType: 'json',
            method: 'POST',
            data: $scope.project,
            headers: {
                "Content-Type": "application/json",
                'X-CSRF-TOKEN': token
            }
        }).success(function(response){
            if(response.hasError){
               $scope.project.error = {};
                for(var i = 0, len = response.errors.length; i < len; i++) {
                    $scope.project.error[response.errors[i].field] = response.errors[i].message;
                }
            } else{
                delete($scope.project);
                $('#newProject').modal('hide');
            }
        }).error(function(error){
            $scope.error = error;
        });

    };

    $scope.closeCreateProject = function () {
        $scope.project = {};
    }




});



app.controller('forms', function($scope, $http) {

    //Get all projects
    $http({
        method: 'GET',
        url: 'http://localhost:8080/api/projects'
    }).then(function successCallback(response) {
        $scope.projects = response.data;
        console.log(response);
    }, function errorCallback(response) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
    });





    $scope.form = {projectIds:[]};

    $scope.createForm = function () {

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

            console.log(response);

            if(response.hasError){
                $scope.form.error = {};
                for(var i = 0, len = response.errors.length; i < len; i++) {
                    $scope.form.error[response.errors[i].field] = response.errors[i].message;
                }
            } else{
                delete($scope.form);
                $('#newForm').modal('hide');
            }
        }).error(function(error){
            $scope.error = error;
        });

    }

    $scope.closeCreateForm = function () {
        $scope.form = {projectIds:[]};
    }



});




app.controller('createForm', function($scope, $http) {

    var formId = $("meta[name='_form_id']").attr("content");

    $scope.options = {};

    $http({
        method: 'GET',
        url: 'http://localhost:8080/api/forms/'+formId,
    }).then(function successCallback(response) {
        $scope.form = response.data;
        $scope.options.lastAddedID = response.data.fields.length;

        //add is req from validation arr
        for(var i = 0; i < $scope.form.fields.length; i++){
        for(var j = 0; j < $scope.form.fields[i].validations.length; j++) {
            if($scope.form.fields[i].validations[j].key == "required"){
                $scope.form.fields[i].is_required = true;
            }

            if($scope.form.fields[i].validations[j].key == "file_size"){
                $scope.form.fields[i].uploadLimit = $scope.form.fields[i].validations[j].value;
            }

            if($scope.form.fields[i].validations[j].key == "file_formats"){

                var fobj = {};
                var arr =  $scope.form.fields[i].validations[j].value.split(",");

                for (var av in  arr) {
                    fobj[arr[av]] = true;
                }

                $scope.form.fields[i].files = fobj;
                }
            }
        }


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

        if(field.type == "check"){
            var vKey = "min_checks";
            var vValue = $scope.options.validationRule;
        }

        if(typeof vValue == 'undefined' || vValue == "null" && field.type == "check"){
            field.validationErr = "Please select correct value.";
            return;
        }

        if(typeof vKey == 'undefined' || vKey == "null"){
            field.validationErr = "Please select valid rule.";
            return;
        }

        if(typeof vValue == 'undefined' && field.type != "check"){
            field.validationErr = "Rule value is required.";
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

        //add for check
        if(field.type == "check"){
            field.validations.push({key: vKey, value: $scope.options.validationRule});
            return;
        }

        //datatype validation
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

        if(rule == "required"){
            field.is_required = false;
        }

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
            "label" : "New field - " + $scope.options.lastAddedID,
            "value": null,
            "hasError": null,
            "errMsg": null,
            "helpText": null,
            "options": [],
            "validations": []
        };





        if(fieldType == "radio" || fieldType == "check" || fieldType == "select"){
            newField.options = [{id:1,label:"Choice 1", value:"1"}, {id:2,label:"Choice 2", value:"2"}, {id:3,label:"Choice 3", value:"3"}];
        }

        if(fieldType == "upload"){
            newField.validations = [{key:"file_size",value:2}, {key:"file_formats",value:"docx"}];
            newField.uploadLimit = '2';
            newField.files = {"docx":true};
        }

        $scope.form.fields.push(newField);

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
            $scope.response = response;
        }).error(function(error){
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
            $scope.response = response;
        }).error(function(error){
            $scope.error = error;
        });


    }


    $scope.deleteField = function (field){
        field_id = field.id;
        for(var i = 0; i < $scope.form.fields.length; i++){
            if($scope.form.fields[i].id == field_id){
                $scope.form.fields.splice(i, 1);
                break;
            }
        }

        $scope.updateForm();
    }


    // add new option to the field
    $scope.addOption = function (field){

        if(!field.options)
            field.options = new Array();

        var lastOptionID = 0;

        if(field.options[field.options.length-1])
            lastOptionID = field.options[field.options.length-1].id;

        // new option's id
        var option_id = lastOptionID + 1;

        var newOption = {
            "id" : option_id,
            "label" : "Choice " + option_id,
            "value" : "Choice value"
        };

        // put new option into field_options array
        field.options.push(newOption);
    }

    // delete particular option
    $scope.deleteOption = function (field,option){
        for(var i = 0; i < field.options.length; i++){
            if(field.options[i].id == option.id){
                field.options.splice(i, 1);
                break;
            }
        }
    }


    $scope.updateUploadFormat = function (field){

        var file_format_arr = [];
        for (var format in  field.files) {
            if(field.files[format] == true){
                file_format_arr.push(format);
            }
        }
        $scope.removeValidationRule(field, "file_formats");
        field.validations.push({key: "file_formats", value: file_format_arr.join()});
    }

    $scope.updateUploadSize = function (field){
        $scope.removeValidationRule(field, "file_size");
        field.validations.push({key: "file_size", value: field.uploadLimit});
    }




    $scope.sortableOptions = {
        update: function(e, ui) {
            $scope.updateForm();
        }
    };




});



app.directive('fieldDirective', function($http, $compile) {

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