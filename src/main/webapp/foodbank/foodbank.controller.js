/**
 * Foodbank controller. This will handle displaying the foobank
 * and any front end functionality associated with it.
 * Created by alexgreco on 3/9/17.
 */
(function(){
    'use strict';

    angular.module('asacs')
        .controller('foodbankController', FoodbankController);

    FoodbankController.$inject = ['$scope', '$http', 'authService'];

    function FoodbankController($scope, $http, authService){

        var user_name = authService.getUserAuthName();
        var site = authService.getUserSite();
        //$scope.parent_site = site;
        $scope.user_name = user_name;

        //variables for div selection
        $scope.createRequest = false;
        $scope.requestStatus = false;
        $scope.outRequest = false;
        $scope.viewInv = false;
        $scope.addInv = false;
        $scope.editInv = false;
        $scope.localFoodbankExists = false;
        $scope.showLocalInv = false;
        $scope.showExtInv = false;
        $scope.addFood = false;
        $scope.addSupply = false;
        $scope.itemExp = new Date();
        $scope.supplyDate = false;
        $scope.foodDate = false;
        $scope.editItemID = false;
        $scope.requestItemID = false;
        $scope.genReqItemID = false;
        $scope.genReqItemName = false;
        $scope.genReqItemUnits = 0;
        $scope.genReqCurrUnits = 0;
        $scope.requestStatus = false;
        $scope.fillReq = false;
        $scope.inventory = [];
        $scope.todaysDate = new Date();
        $scope.allOtherSites = [{value:'',label:'Site Name'}];

        //$scope.todaysDate = $scope.itemExp.getFullYear()+"-"+($scope.itemExp.getMonth()+1)+"-"+$scope.itemExp.getDate();

        //used when waiting for api call
        $scope.loadingResources = false;

        //add all functions that need to be public
        var exports = {
            getFoodbanks : getFoodbanks,
            showOption: showOption,
            createRequest: createRequest,
            checkLocalFoodbank: checkLocalFoodbank,
            getLocalInventory: getLocalInventory,
            getExternalInventory: getExternalInventory,
            checkInventorySource: checkInventorySource,
            addFoodItem: addFoodItem,
            addSupplyItem: addSupplyItem,
            insertNewItem: insertNewItem,
            setNonExpDate: setNonExpDate,
            editLocalItem: editLocalItem,
            generateRequest: generateRequest,
            updateItem: updateItem,
            hideAllOptions: hideAllOptions,
            requestStatus: requestStatus,
            cancelRequest: cancelRequest,
            outstandingRequests: outstandingRequests,
            rejectRequest: rejectRequest,
            fillRequest: fillRequest,
            updateRequest: updateRequest,
            getInvDate: getInvDate,
            setExpiredDate: setExpiredDate,
            getAllOtherSites: getAllOtherSites
        };

        /////////////////////////////

        /**
         * Hide all the sections in the
         * body section of the page
         */
        function hideAllOptions(){
            $scope.createRequest = false;
            $scope.requestStatus = false;
            $scope.outRequest = false;
            $scope.viewInv = false;
            $scope.addInv = false;
            $scope.editInv = false;
            $scope.showLocalInv = false;
            $scope.showExtInv = false;
            $scope.addFood = false;
            $scope.addSupply = false;
            $scope.successMessage = false;
            $scope.nonSuccessMessage = false;
            $scope.supplyDate = false;
            $scope.foodDate = false;
            $scope.loadingResources = false;
            $scope.outRequest = false;
            $scope.fillReq = false;
        }

        /**
         * Take in what section we want to show.
         * Hide all other sections.
         */
        function showOption(option){
            //first hide all options
            hideAllOptions();

            //next set the correct div var to true
            switch(option){
                case("createRequest"):
                    $scope.createRequest = true;
                    break;
                case("requestStatus"):
                    $scope.requestStatus = true;
                    break;
                case("outRequest"):
                    $scope.outRequest = true;
                    break;
                case("viewInv"):
                    $scope.viewInv = true;
                    break;
                case("addInv"):
                    $scope.addInv = true;
                    break;
                break;
                default:
                    break;
            }
        };

        function checkLocalFoodbank(){

            //hide everything in the view
            hideAllOptions();
            //show loading banner
            $scope.loadingResources = true;

            //make api request
            $http.get("webapi/foodbank/" + site)
                .then( function(response){
                    //add identifier if value of the service is false
                    for (var object in response.data) {
                        if (response.data[object] == site) {
                            $scope.localFoodbankExists = true;
                        } else {
                            $scope.localFoodbankExists = false;
                        }
                    }
                    //turn loading banner off
                    $scope.loadingResources = false;
                });
        }

        /*
        * get list of all sites for Inventory Site Search option
        */
        function getAllOtherSites(){
            $http.get("webapi/foodbank/sites/" + site)
                .then( function(response) {
                    $scope.allOtherSites = response.data;
                })
        }


        /**
         * check to see if user can add Inventory, if so show addInv tab
         */
        function checkInventorySource(){

            //hide everything in the view
            hideAllOptions();
            //show loading banner
            $scope.loadingResources = true;

            //make api request
            $http.get("webapi/foodbank/" + site)
                .then( function(response){
                    //add identifier if value of the service is false
                    for(var service in response.data){
                        if (response.data[service] === false){
                            //show checkbox for LocalInventory
                            switch (service) {
                                case("foodbank"):
                                    $scope.localFoodbankExists = true;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    //turn loading banner off
                    $scope.loadingResources = false;
                    $scope.addInv = true;
                });
        }

        // This function just exposes the Add Food container
        function addFoodItem(){
            hideAllOptions();
            //wipe form items
            $scope.itemName = null;
            $scope.itemUnits = null;
            $scope.itemExp = new Date;
            $scope.storageType = null;
            $scope.supplyType = null;
            $scope.foodType = null;
            $scope.addInv = true;
            //$scope.foodDate = true;
            $scope.addFood = true;
            setNonExpDate(false);
        }

        // This function exposes the Add Supply container
        function addSupplyItem(){
            hideAllOptions();
            //wipe form items
            $scope.itemName = null;
            $scope.itemUnits = null;
            $scope.itemExp = new Date;
            $scope.storageType = null;
            $scope.supplyType = null;
            $scope.foodType = null;
            $scope.addInv = true;
            //$scope.supplyDate = true;
            $scope.addSupply = true;
            setNonExpDate(false);
        }

        /*
        * This function is used to set the expiration date to 9999-01-01 for non-expiring items added to inventory
        */
        function setNonExpDate(option){
            //next set the correct div var to true
            switch(option){
                case(true):
                    $scope.expDateMin = new Date(9999, 0, 1);
                    $scope.expDateMax = null;
                    break;
                case(false):
                    $scope.expDateMax = null;
                    $scope.expDateMin = null;
                    break;
                default:
                    break;
            }

        }

        /*
         * function sets search Max expiration to today's date
         */
        function setExpiredDate(option){
            //next set the correct div var to true
            switch(option){
                case(true):
                    $scope.expDateMax = new Date;
                    $scope.expDateMin = null;
                    break;
                case(false):
                    $scope.expDateMax = null;
                    $scope.expDateMin = null;
                    break;
                default:
                    break;
            }

        }

        // This function inserts new Inventory Item into DB

        function insertNewItem(){

            //build itemObject we will pass to the back end
            var insertItem = {
                parent_site: site,
                item_name: $scope.itemName,
                units: $scope.itemUnits,
                exp_date: $scope.itemExp,
                storage_type: $scope.storageType,
                supply_type: $scope.supplyType,
                food_type: $scope.foodType
            };

            //convert js obj to json
            var finalVar = angular.toJson(insertItem);

            //call the api call to add a item into inventory
            $http.post('webapi/foodbank/inventory/insert', finalVar)
                .then(function(response){
                    //show success message or failure
                    if(response.status === 200){
                        $scope.itemName = null;
                        $scope.itemUnits = null;
                        $scope.itemExp = null;
                        $scope.storageType = null;
                        $scope.supplyType = null;
                        $scope.foodType = null;

                        hideAllOptions();
                        $scope.successMessage = true;
                    }else{

                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * function to call the foodbank api.
         * A json object will be returned in
         * the response.data obj.
         */
        function getFoodbanks(){
            $http.get("webapi/foodbank")
                .then( function(response){
                    console.log(response.data);
                    $scope.newVar = response.data;
                });
        };

        /*
         *
         * This sets up Live Search for Item Name
         * we shall see...
         *
         */

        // function for Angular to convert string to comparable date
        function getInvDate(string){
            return new Date(string);
        }

        // variables for Item Search functions
        $scope.sortType = 'item_name';
        $scope.sortReverse = false;
        $scope.searchName = "";
        $scope.localSearch = "";
        $scope.unitsMin = null;
        $scope.unitsMax = null;
        $scope.searchExp = false;
        $scope.searchNonExp = false;
        $scope.expDateMax = null;
        $scope.expDateMin = null;
        setNonExpDate(false);
        $scope.foodSupply = "";
        $scope.foodType = "";
        $scope.supplyType = "";
        $scope.storageType = "";
        $scope.parentSite = "";
        $scope.archived = false;
        // function to change scope.item
        $scope.change = function() {

            var searching = {
                'searchName': $scope.searchName,
                'site_id': site,
                'localSearch': $scope.localSearch,
                //'unitsMin': $scope.unitsMin,
                //'unitsMax': $scope.unitsMax,
                'expDateMax': $scope.expDateMax,
                'expDateMin': $scope.expDateMin,
                'foodSupply': $scope.foodSupply,
                'foodType': $scope.foodType,
                'supplyType': $scope.supplyType,
                'storageType': $scope.storageType,
                'parentSite': $scope.parentSite,
                'archived': $scope.archived
            };

            var finalVar = angular.toJson(searching);

            $http.put('webapi/foodbank/inventory/search', finalVar)
                .then(function(response){
                    $scope.inventory.length = 0;
                    for(var item in response.data['inventory']){
                        $scope.inventory.push(response.data['inventory'][item]);
                    }

            });
        };

        /*
        * This function kicks of search on dates in Inv screens
        */


        /**
         * This function is used to get local Inventory
         */
        function getLocalInventory(){
            hideAllOptions();
            $scope.loadingResources = true;
            // clear sort and filters
            $scope.sortType = 'item_name';
            $scope.sortReverse = false;
            $scope.searchName = "";
            $scope.localSearch = true;
            $scope.unitsMin = null;
            $scope.unitsMax = null;
            $scope.expDateMax = null;
            $scope.expDateMin = null;
            $scope.foodSupply = "";
            $scope.foodType = "";
            $scope.supplyType = "";
            $scope.storageType = "";
            $scope.archived = false;
            //get the data for this site's shelter
            $http.get('webapi/foodbank/inventory/local/' + site)
                .then(function(response){
                    $scope.inventory.length = 0;
                    for(var item in response.data['inventory']){
                        $scope.inventory.push(response.data['inventory'][item]);
                    }

                    $scope.loadingResources = false;
                    $scope.viewInv = true;
                    $scope.showLocalInv = true;
                });
        }
        /**
         * This function is used to get external Inventory
         */
        function getExternalInventory(){
            hideAllOptions();
            $scope.loadingResources = true;
            // clear sort and filters
            $scope.sortType = 'item_name';
            $scope.sortReverse = false;
            $scope.searchName = "";
            $scope.localSearch = false;
            $scope.unitsMin = null;
            $scope.unitsMax = null;
            $scope.expDateMax = null;
            $scope.expDateMin = null;
            $scope.foodSupply = "";
            $scope.foodType = "";
            $scope.supplyType = "";
            $scope.storageType = "";
            $scope.archived = false;

            //get the data for this site's shelter
            $http.get('webapi/foodbank/inventory/external/' + site)
                .then(function(response){
                    $scope.inventory.length = 0;
                    for(var item in response.data['inventory']){
                        $scope.inventory.push(response.data['inventory'][item]);
                    }

                    $scope.loadingResources = false;
                    $scope.viewInv = true;
                    $scope.showExtInv = true;
                });
        }

        /*
        * Edit local inventory
        */
        function editLocalItem(item_id) {
            hideAllOptions();
            $scope.loadingResources = true;

            //get the data for this site's shelter
            $http.get('webapi/foodbank/inventory/item/' + item_id)
                .then(function(response){
                    $scope.editItemID = response.data['item_id'];
                    $scope.editItemName = response.data['item_name'];
                    $scope.editItemUnits = response.data['units'];
                    $scope.editItemExp = response.data['exp_date'];

                    $scope.loadingResources = false;
                    $scope.editItemID = item_id;
                    $scope.editInv = true;
                });
        }

        /*
        *function to update item qty of units
        */

        function updateItem() {

            if($scope.editItemUnits >= 0) {
                //build object to send to API
                var item = {
                    'item_id': $scope.editItemID,
                    'units': $scope.editItemUnits
                };

                //convert js obj to json
                var finalVar = angular.toJson(item);

                //call API
                $http.put('webapi/foodbank/inventory/update', finalVar)
                    .then(function (response) {
                        //clear fields
                        $scope.editItemID = null;
                        $scope.editItemName = null;
                        $scope.editItemUnits = null;

                        //Handle the response
                        if (response.status === 200) {
                            //hide everything
                            hideAllOptions();
                            //show the success message
                            $scope.successMessage = true;
                        } else {
                            hideAllOptions();
                            $scope.nonSuccessMessage = true;
                        }
                    });

            }
            if($scope.editItemUnits == 0) {
                var item = {
                    'item_id': $scope.editItemID
                };

                //convert js obj to json
                var finalVar = angular.toJson(item);

                //call API
                $http.put('webapi/foodbank/inventory/delete', finalVar)
                    .then(function (response) {
                        //clear fields
                        $scope.editItemID = null;
                        $scope.editItemName = null;
                        $scope.editItemUnits = null;

                        //Handle the response
                        if (response.status === 200) {
                            //hide everything
                            hideAllOptions();
                            //show the success message
                            $scope.successMessage = true;
                        } else {
                            hideAllOptions();
                            $scope.nonSuccessMessage = true;
                        }
                    });
            }
        }

        /*
         * generate request for inventory
         */
        function generateRequest(item_id) {
            hideAllOptions();
            $scope.loadingResources = true;

            //get the data for this site's shelter
            $http.get('webapi/foodbank/inventory/item/' + item_id)
                .then(function(response){
                    var items = [];
                    $scope.genReqItemID = response.data['item_id'];
                    $scope.genReqItemName = response.data['item_name'];
                    $scope.genReqItemUnits = response.data['units'];
                    $scope.genReqCurrUnits = response.data['units'];
                    $scope.genReqItemExp = response.data['exp_date'];

                    $scope.loadingResources = false;
                    $scope.requestItemID = item_id;
                    $scope.createRequest = true;
                });
        }

        /**
         * Call this function when you want to add a Insert new Request into the DB
         */
        function createRequest(){
            //hide everything in the view
            hideAllOptions();
            //show loading banner
            $scope.loadingResources = true;
            //build pantry object we will pass to the back end

            var request = {
                requesting_user: user_name,
                requested_item_id: $scope.genReqItemID,
                requested_units: $scope.genReqItemUnits
            };

            //Convert the js obj to json
            var finalVar = angular.toJson(request);

            //send the post request
            $http.post('webapi/foodbank/request/create', finalVar)
                .then(function(response){

                    //show success message or failure
                    if(response.status === 200){
                        //clear all form elements
                        $scope.requesting_user = null;
                        $scope.requested_item_id = null;
                        $scope.requested_units = null;
                        hideAllOptions();
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /**
         * This function is used to get local request status
         */
        function requestStatus(){
            hideAllOptions();
            $scope.loadingResources = true;
            $scope.sortType = 'request_id';
            $scope.sortReverse = false;
            $scope.searchName = "";

            //get the data for this site's shelter
            $http.get('webapi/foodbank/request/status/' + site)
                .then(function(response){
                    var requests = [];
                    for(var request in response.data['requests']) {
                        requests.push(response.data['requests'][request]);
                    }
                    //console.log(items);
                    $scope.requests = requests;

                    $scope.loadingResources = false;
                    $scope.requestStatus = true;
                });
        }

        /**
         * This function is used to get outstanding requests
         */
        function outstandingRequests(){
            hideAllOptions();
            $scope.loadingResources = true;
            $scope.sortType = 'request_id';
            $scope.sortReverse = false;
            $scope.searchName = "";

            //get the data for this site's shelter
            $http.get('webapi/foodbank/request/outstanding/' + site)
                .then(function(response){
                    var requests = [];
                    for(var request in response.data['requests']) {
                        requests.push(response.data['requests'][request]);
                    }
                    //console.log(items);
                    $scope.requests = requests;

                    $scope.loadingResources = false;
                    $scope.outRequest = true;
                });
        }

        /*
         * function to delete/cancel request
         * */
        function rejectRequest(requestID) {
            var requestCancel = {
                'request_id': requestID,
                'approving_user': user_name
            };

            //convert js obj to json
            var finalVar = angular.toJson(requestCancel);

            //call API
            $http.put('webapi/foodbank/request/reject', finalVar)
                .then(function (response) {

                    //Handle the response
                    if (response.status === 200) {
                        //hide everything
                        hideAllOptions();
                        //show the success message
                        $scope.successMessage = true;
                    } else {
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });
        }

        /*
        * function to delete/cancel request
        * */
        function cancelRequest(requestID) {
            var requestCancel = {
                'request_id': requestID,
                'fulfilled_units': $scope.fulfilled_units
            };

            //convert js obj to json
            var finalVar = angular.toJson(requestCancel);

            //call API
            $http.put('webapi/foodbank/request/delete', finalVar)
                .then(function (response) {

                    //Handle the response
                    if (response.status === 200) {
                        //hide everything
                        hideAllOptions();
                        //show the success message
                        $scope.successMessage = true;
                    } else {
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });
            }

         /*
         * function to fulfill request
         */
        function fillRequest(requestID) {
            hideAllOptions();
            $scope.loadingResources = true;

            //get the data for this site's shelter
            $http.get('webapi/foodbank/request/fill/' + requestID)
                .then(function(response){
                    $scope.fillRequestID = response.data['request_id'];
                    $scope.fillItemName = response.data['item_name'];
                    $scope.fillRequestedUnits = response.data['requested_units'];
                    $scope.fillItemID = response.data['item_id'];
                    $scope.fillFulfilledUnits = response.data['fulfilled_units'];
                    $scope.fillRequestingFoodbank = response.data['requesting_foodbank'];
                    $scope.fillAvailableUnits = response.data['available_units'];
                    $scope.fillRequestStatus = response.data['request_status'];

                    //Set max fill to either Available Units or Requested Units
                    // which ever is least
                    if($scope.fillAvailableUnits >= $scope.fillRequestedUnits){
                        $scope.maxfill = $scope.fillRequestedUnits;
                    } else {
                        $scope.maxfill = $scope.fillAvailableUnits;
                    }

                    $scope.loadingResources = false;
                    $scope.fillTheseUnits = $scope.maxfill;
                    $scope.fillReq = true;
                });
        }

        function updateRequest(requestID, fillUnits, prevfilled, availUnits, reqUnits, fillItemID) {
            hideAllOptions();
            $scope.loadingResources = true;

            var requestFill = {
                'request_id': requestID,
                'fill_units': fillUnits,
                'fulfilled_units': prevfilled,
                'available_units': availUnits,
                'requested_units': reqUnits,
                'requested_item_id': fillItemID,
                'approving_user': user_name
            };

            //convert js obj to json
            var requestVar = angular.toJson(requestFill);

            //call API
            $http.put('webapi/foodbank/request/update', requestVar)
                .then(function (response) {

                    //Handle the response
                    if (response.status === 200) {
                        //hide everything
                        hideAllOptions();
                        $scope.fillRequestID = "";
                        $scope.fillItemName = "";
                        $scope.fillRequestedUnits = "";
                        $scope.fillItemID = "";
                        $scope.fillFulfilledUnits = "";
                        $scope.fillRequestingFoodbank = "";
                        $scope.fillAvailableUnits = "";
                        $scope.fillRequestStatus = "";
                        $scope.fillTheseUnits = "";
                        $scope.maxfill = "";

                        $scope.loadingResources = false;
                        //show the success message
                        $scope.successMessage = true;
                    } else {
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }
                });


        }
        //return functions so they are available to the app
        return exports;
    };

})();