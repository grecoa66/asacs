/**
 * Services controller. This controller will handle
 * all of the front end functionality for the services.
 * Created by alexgreco on 3/10/17.
 */
(function(){
    'use strict';

    angular.module('asacs')
        .controller('servicesController', ServicesController);

    ServicesController.$injector = ['$scope', '$http', 'authService'];

    function ServicesController($scope, $http, authService){
        //get user's parent_site and username
        var user_name = authService.getUserAuthName();
        var site = authService.getUserSite();
        //variables for div selection
        $scope.addServ = false;
        $scope.editServ = false;
        $scope.deleteServ = false;

        //used when waiting for api call
        $scope.loadingResources = false;

        //used to store which service is selected
        $scope.servSelect = null;

        //for success modal
        $scope.successMessage = false;

        //for api failure
        $scope.nonSuccessMessage = false;

        /**
         * add all functions that need to be public
         * and available to the view (html page)
         */
        var exports = {
            hideAllOptions: hideAllOptions,
            addService: addService,
            editService: editService,
            deleteService: deleteService,
            checkAvailableService: checkAvailableService,
            addPantry: addPantry,
            addShelter: addShelter,
            addKitchen: addKitchen,
            addFoodbank: addFoodbank,
            updatePantry: updatePantry,
            updateShelter: updateShelter,
            updateKitchen: updateKitchen,
            deleteFoodbank: deleteFoodbank,
            deletePantry: deletePantry,
            deleteShelter: deleteShelter,
            deleteKitchen: deleteKitchen
        };

        ////////////////////////////////////////////////////////

        /**
         * This function will call the api to determine what
         * services exist. The response
         * will have the name of the service as an identifier and
         * a boolean value describing if one exists or not.
         */
        function checkAvailableService(action){

            //hide everything in the view
            hideAllOptions();
            //show loading banner
            $scope.loadingResources = true;
            //var to track how many services are available
            $scope.numServices = 0;

            //make api request
            $http.get("webapi/services/" + site)
                .then( function(response){
                    //boolean to determine if any services are available
                    var serviceAvailable = false;

                    //add identifier if value of the service is false
                    for(var service in response.data){
                        if (response.data[service] === false){
                            //show checkbox for services that can be added
                            switch (service) {
                                case("foodbank"):
                                    $scope.noFoodbankExists = true;
                                    serviceAvailable = true;
                                    break;
                                case("pantry"):
                                    $scope.noPantryExists = true;
                                    serviceAvailable = true;
                                    break;
                                case("shelter"):
                                    $scope.noShelterExists = true;
                                    serviceAvailable = true;
                                    break;
                                case("kitchen"):
                                    $scope.noKitchenExists = true;
                                    serviceAvailable = true;
                                    break;
                                default:
                                    break;
                            }
                        }else{
                            //increiment the number of service that exist
                            $scope.numServices++
                        }
                    }

                    //turn loading banner off
                    $scope.loadingResources = false;

                    //if no services are available warn the user
                    if(!serviceAvailable && action === 'add'){
                        /*
                         * This is shown because there are no more
                         * service the user can add for this site
                         */
                        $scope.noServiceRemain = true;
                    }else{
                        //display which form you want
                        switch(action) {
                            case('add'):
                                //show the add service check boxes
                                $scope.addServ = true;
                                break;
                            case('edit'):
                                //show the edit service check boxes
                                $scope.editServ = true;
                                break;
                            case('delete'):
                                //show the delete service check boxes
                                $scope.deleteServ = true;
                                break;
                            default:
                                hideAllOptions();
                                break;
                        }
                    }
                });
        }

        /**
         * Hide all the sections in the
         * body section of the page.
         * This clears the page off all its content
         */
        function hideAllOptions(){
            //variables for div selection
            $scope.addServ = false;
            $scope.noServiceRemain = false;
            $scope.editServ = false;
            $scope.deleteServ = false;
            //status banners
            $scope.lastServiceWarning = false;
            $scope.successMessage = false;
            $scope.nonSuccessMessage = false;
            //all add service forms
            $scope.pantryDetail = false;
            $scope.shelterDetail = false;
            $scope.kitchenDetail = false;
            $scope.foodbankDetail = false;
            //all edit forms
            $scope.editPantryForm = false;
            $scope.editShelterForm = false;
            $scope.editKitchenForm = false;
            $scope.editFoodbank = false;
            //all delete conformations
            $scope.deleteFoodbankConfirm = false;
            $scope.deletePantryConfirm = false;
            $scope.deleteShelterConfirm = false;
            $scope.deleteKitchenConfirm = false;

        }

        /**
         * This function will take user input
         * from the front end and call the create
         * service api method. Depending on the type of
         * service, create the correct service.
         */
        function addService(){

            if($scope.servSelect === null){
                return;
            }else{
                switch ($scope.servSelect) {
                    case("foodbank"):
                        $scope.addServ = false;
                        $scope.foodbankDetail = true;
                        break;
                    case("pantry"):
                        //hide the selection box and show the details view
                        $scope.addServ = false;
                        $scope.pantryDetail = true;
                        break;
                    case("shelter"):
                        $scope.addServ = false;
                        $scope.shelterDetail = true;
                        $scope.shelterBunks = 0;
                        $scope.shelterRooms = 0;
                        break;
                    case("kitchen"):
                        $scope.addServ = false;
                        $scope.kitchenDetail = true;
                        break;
                    default:
                        break;
                }
            }
        }

        /**
         * Call this function when you want to add a pantry to the DB
         */
        function addPantry(){

            //build pantry object we will pass to the back end
            var pantry = {
                parent_site: site,
                description: $scope.pantryDesc,
                hours: $scope.pantryHours,
                pantry_condition: $scope.pantryCond
            };

            //Convert the js obj to json
            var finalVar = angular.toJson(pantry);

            //send the post request
            $http.post('webapi/pantry/add', finalVar)
                .then(function(response){

                    //show success message or failure
                    if(response.status === 200){
                        $scope.noPantryExists = false;
                        //clear all form elements
                        $scope.pantryDesc = null;
                        $scope.pantryHours = null;
                        $scope.pantryCond = null;

                        hideAllOptions();

                        $scope.pantryDetail = false;
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.pantryDetail = false;
                        $scope.nonSuccessMessage = true;
                    }
            });
        }

        /**
         * Call this function when you want to add a shelter to the DB
         */
        function addShelter(){

            //build shelter object we will pass to the back end
            var shelter = {
                parent_site: site,
                description: $scope.shelterDesc,
                hours: $scope.shelterHours,
                shelter_condition: $scope.shelterCond,
                bunks: $scope.shelterBunks,
                rooms: $scope.shelterRooms
            };

            //convert js obj to json
            var finalVar = angular.toJson(shelter);

            $http.post('webapi/shelter/add', finalVar)
                .then(function(response){

                    //show success message or failure
                    if(response.status === 200){
                        $scope.noShelterExists = false;
                        //clear all form elements
                        $scope.shelterDesc = null;
                        $scope.shelterHours = null;
                        $scope.shelterCond = null;
                        $scope.shelterBunks = 0;
                        $scope.shelterRooms = 0;
                        hideAllOptions();

                        $scope.shelterDetail = false;
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.shelterDetail = false;
                        $scope.nonSuccessMessage = true;
                    }
                });

        }

        /**
         * Call this function when you want to add a soup kitchen to the DB
         */
        function addKitchen(){

            //build shelter object we will pass to the back end
            var kitchen = {
                parent_site: site,
                description: $scope.kitchenDesc,
                hours: $scope.kitchenHours,
                kitchen_condition: $scope.kitchenCond,
                seating_capacity: $scope.kitchenCap
            };

            //convert js obj to json
            var finalVar = angular.toJson(kitchen);

            //call the api call to add a new soup kitchen
            $http.post('webapi/kitchen/add', finalVar)
                .then(function(response){
                    //show success message or failure
                    if(response.status === 200){
                        $scope.noKitchenExists = false;
                        //clear all form elements
                        $scope.kitchenDesc = null;
                        $scope.kitchenHours = null;
                        $scope.kitchenCond = null;
                        $scope.kitchenCap = null;

                        hideAllOptions();

                        $scope.kitchenDetail = false;
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.kitchenDetail = false;
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * Call this function when you cant to add a foodbank to the db
         */
        function addFoodbank(){

            //build shelter object we will pass to the back end
            var foodbank = {
                parent_site: site
            };

            //convert js obj to json
            var finalVar = angular.toJson(foodbank);

            //call api to add a foodbank
            $http.post('webapi/foodbank/add', finalVar)
                .then(function(response){
                    //show success message or failure
                    if (response.status === 200) {
                        $scope.noFoodbankExists = false;
                        hideAllOptions();
                        $scope.foodbankDetail = false;
                        $scope.successMessage = true;
                    } else {
                        hideAllOptions();
                        $scope.foodbankDetail = false;
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * This function will handle what service the user wants to edit.
         * It will also pass the buck along to the correct method for
         */
        function editService(){
            if($scope.servEdit === null){
                return;
            }else{
                switch ($scope.servEdit) {
                    case("foodbank"):
                        $scope.editServ = false;
                        editFoodbank();
                        break;
                    case("pantry"):
                        //hide the selection box and show the details view
                        $scope.editServ = false;
                        getPantryEditDetail();
                        break;
                    case("shelter"):
                        $scope.editServ = false;
                        getShelterEditDetail();
                        break;
                    case("kitchen"):
                        $scope.editServ = false;
                        getKitchenEditDetail();
                        break;
                    default:
                        break;
                }
            }
        }

        /**
         * This function will get all the details of the pantry
         * corresponding to the current user's site. It will
         * Assign the returned values to scope variables that will
         * ve used in the view for editing
         */
        function getPantryEditDetail(){

            //get the data for this site's pantry
            $http.get('webapi/pantry/' + site)
                .then(function(response){
                    $scope.parentSite = site;
                    $scope.pantryEditDesc = response.data['description'];
                    $scope.pantryEditCond = response.data['pantry_condition'];
                    $scope.pantryEditHours = response.data['hours'];

                    hideAllOptions();
                    $scope.editPantryForm = true;
                });
        }

        /**
         * This function is used to send an update api request for pantry
         */
        function updatePantry(){

            //build the obj to send to the api
            var pantry = {
                'parent_site': $scope.parentSite,
                'description': $scope.pantryEditDesc,
                'hours': $scope.pantryEditHours,
                'pantry_condition': $scope.pantryEditCond
            };

            //Call the pantry update in the api
            $http.put('webapi/pantry/update', pantry)
                .then(function(response){
                    //clear all edit fields
                    $scope.pantryEditDesc = null;
                    $scope.parentSite = null;
                    $scope.pantryEditHours = null;
                    $scope.pantryEditCond = null;

                    //Handle the response
                    if(response.status === 200){
                        //hide everything
                        hideAllOptions();
                        //show the success message
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * This function will get all the details of the shelter
         * corresponding to the current user's site. It will
         * Assign the returned values to scope variables that will
         * ve used in the view for editing
         */
        function getShelterEditDetail(){

            //get the data for this site's shelter
            $http.get('webapi/shelter/' + site)
                .then(function(response){
                    $scope.parentSite = site;
                    $scope.shelterEditDesc = response.data['description'];
                    $scope.shelterEditCond = response.data['shelter_condition'];
                    $scope.shelterEditHours = response.data['hours'];
                    $scope.shelterBunks = response.data['bunks'];
                    $scope.shelterRooms = response.data['rooms'];

                    hideAllOptions();
                    $scope.editShelterForm = true;
                });
        }

        /**
         * This function is used to send an update api request for shelter
         */
        function updateShelter(){

            //build the obj to send to the api
            var shelter = {
                'parent_site': $scope.parentSite,
                'description': $scope.shelterEditDesc,
                'hours': $scope.shelterEditHours,
                'shelter_condition': $scope.shelterEditCond,
                'rooms' : $scope.shelterRooms,
                'bunks' : $scope.shelterBunks
            };

            //Call the shelter update in the api
            $http.put('webapi/shelter/update', shelter)
                .then(function(response){
                    //clear all edit fields
                    $scope.shelterEditDesc = null;
                    $scope.shelterSite = null;
                    $scope.shelterEditHours = null;
                    $scope.shelterEditCond = null;
                    $scope.shelterBunks = 0;
                    $scope.shelterRooms = 0;

                    //Handle the response
                    if(response.status === 200){
                        //hide everything
                        hideAllOptions();
                        //show the success message
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * This function will get all the details of the kitchen
         * corresponding to the current user's site. It will
         * Assign the returned values to scope variables that will
         * ve used in the view for editing
         */
        function getKitchenEditDetail(){

            //get the data for this site's kitchen
            $http.get('webapi/kitchen/' + site)
                .then(function(response){
                    $scope.parentSite = site;
                    $scope.kitchenEditDesc = response.data['description'];
                    $scope.kitchenEditCond = response.data['kitchen_condition'];
                    $scope.kitchenEditHours = response.data['hours'];
                    $scope.kitchenEditCap = response.data['seating_capacity'];

                    hideAllOptions();
                    $scope.editKitchenForm = true;
                });
        }

        /**
         * This function is used to send an update api request for kitchen.
         */
        function updateKitchen(){

            //build the obj to send to the api
            var kitchen = {
                'parent_site': $scope.parentSite,
                'description': $scope.kitchenEditDesc,
                'hours': $scope.kitchenEditHours,
                'kitchen_condition': $scope.kitchenEditCond,
                'seating_capacity': $scope.kitchenEditCap
            };

            //Call the shelter update in the api
            $http.put('webapi/kitchen/update', kitchen)
                .then(function(response){
                    //clear all edit fields
                    $scope.kitchenEditDesc = null;
                    $scope.kitchenSite = null;
                    $scope.kitchenEditHours = null;
                    $scope.kitchenEditCond = null;
                    $scope.kitchenEditCap = null;

                    //Handle the response
                    if(response.status === 200){
                        //hide everything
                        hideAllOptions();
                        //show the success message
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * There is nothing to edit in foodbank so just display
         * a message state that and a button to acknowledge that
         * message.
         */
        function editFoodbank(){
            $scope.editFoodbank = true;
        }

        /**
         * This function will handle the user choosing
         * to delete a service from the frontend of the
         * application. It will check to make sure the
         * user does not delete the last service for the site.
         */
        function deleteService(){
            //ensure this is not the last service
            if($scope.numServices > 1){
                switch($scope.servDelete) {
                    case('foodbank'):
                        hideAllOptions();
                        $scope.deleteFoodbankConfirm = true;
                        break;
                    case('pantry'):
                        hideAllOptions();
                        $scope.deletePantryConfirm = true;
                        break;
                    case('kitchen'):
                        hideAllOptions();
                        $scope.deleteKitchenConfirm = true;
                        break;
                    case('shelter'):
                        hideAllOptions()
                        $scope.deleteShelterConfirm = true;
                        break;
                    default:
                        hideAllOptions();
                        break;
                }
            }else{
                /* Hide everything and warn the user there is only
                 * service left and they can not delete it.
                 */
                hideAllOptions();
                $scope.lastServiceWarning = true;
            }
        }

        /**
         * delete the foodbank service for this site
         */
        function deleteFoodbank(){

            $http.delete("webapi/foodbank/delete/" + site)
                .then(function(response){
                    //Handle the response
                    if(response.status === 200){
                        //hide everything
                        hideAllOptions();
                        //show the success message
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * delete the pantry service for this site
         */
        function deletePantry(){

            $http.delete("webapi/pantry/delete/" + site)
                .then(function(response){
                    //Handle the response
                    if(response.status === 200){
                        //hide everything
                        hideAllOptions();
                        //show the success message
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * delete the pantry service for this site
         */
        function deleteShelter(){

            $http.delete("webapi/shelter/delete/" + site)
                .then(function(response){
                    //Handle the response
                    if(response.status === 200){
                        //hide everything
                        hideAllOptions();
                        //show the success message
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * delete the pantry service for this site
         */
        function deleteKitchen(){

            $http.delete("webapi/kitchen/delete/" + site)
                .then(function(response){
                    //Handle the response
                    if(response.status === 200){
                        //hide everything
                        hideAllOptions();
                        //show the success message
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * This allows all the functions to be publicaly
         * available to the view (html page)
         */
        return exports;
    };
})();