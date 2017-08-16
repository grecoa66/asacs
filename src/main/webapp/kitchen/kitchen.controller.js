/**
 * Created by alexgreco on 3/10/17.
 */

(function(){
    'use strict';
    angular.module('asacs')
        .controller('kitchenController', KitchenController);

    KitchenController.$inject = ['$scope', '$http', 'authService'];

    function KitchenController($scope, $http, authService) {
        var currSite = authService.getUserSite();

        //variables for div selection
        $scope.updateSeats = false;

        $scope.currentKitchen;
        $scope.kitchenEditCap;
        //for success
        $scope.successMessage = false;
        //for errors
        $scope.nonSuccessMessage = false;
        $scope.noKitchenMessage = false;
        $scope.invalidSeatsMessage = false;

        //add all functions that need to be public
        var exports = {
            hideAllOptions: hideAllOptions,
            showOption: showOption,
            getSoupKitchen: getSoupKitchen,
            updateSoupKitchen: updateSoupKitchen,
            showReEnter: showReEnter,
            searchClientByID: searchClientByID,
            searchClientByName: searchClientByName,
            serviceLogUpdate : serviceLogUpdate,
            submitServicesUsed: submitServicesUsed,
            cleanUp : cleanUp
        };

        /////////////////////////////

        /**
         * Take in what section we want to show.
         * Hide all other sections.
         */
        function showOption(option) {
            //first hide all options
            hideAllOptions();

            //next set the correct div var to true
            switch (option) {
                case('successMessage'):
                    $scope.successMessage = true;
                    break;
                case('nonSuccessMessage'):
                    $scope.nonSuccessMessage = true;
                    break;
                case('noKitchenMessage'):
                    $scope.noKitchenMessage = true;
                    break;
                case('invalidSeatsMessage'):
                    $scope.invalidSeatsMessage = true;
                    break;
                case('updateSeats'):
                    $scope.updateSeats = true;
                    break;
                case('clientCheckIn'):
                    checkClientInForms();
                    break;
                default:
                    break;
            }
        };

        function showReEnter() {
            //first hide all options
            hideAllOptions();
            // have them reenter
            $scope.invalidSeatsMessage = true;
            $scope.updateSeats = true;
        };

        /**
         * Hide all the sections in the
         * body section of the page
         */
        function hideAllOptions() {
            $scope.updateSeats = false;
            $scope.successMessage = false;
            $scope.nonSuccessMessage = false;
            $scope.noKitchenMessage = false;
            $scope.invalidSeatsMessage = false;
            $scope.clientSearchInputForm = false;
            $scope.loadingResources = false;
            $scope.clientList = false;
            $scope.kitchenError = false;
            $scope.newServicesUsedReport = false;
            $scope.clientCheckInSuccess = false;
        };


        /**
         * function to call the kitchen api.
         * A json object will be returned in
         * the response.data obj.
         */
        function getSoupKitchen() {
            $http.get("webapi/kitchen/" + currSite)
                .then(function (response) {
                    console.log(response.data);
                    if (response.data['parent_site'] == null) {
                        hideAllOptions();
                        $scope.noKitchenMessage = true;
                    } else {
                        $scope.currentKitchen = response.data;
                        $scope.kitchenEditCap = response.data['seating_capacity'];
                    }
                });
        };

        /**
         * function to call the update kitchen api.
         * A json object will be returned in
         * the response.data obj.
         */
        function updateSoupKitchen() {
            //build kitchen object we will pass to the back end
            var kitchen = {
                parent_site: currSite,
                description: $scope.currentKitchen.description,
                hours: $scope.currentKitchen.hours,
                kitchen_condition: $scope.currentKitchen.kitchen_condition,
                seating_capacity: $scope.kitchenEditCap
            };

            if (kitchen.seating_capacity == null) {
                showReEnter();
            } else {
                $http.put('webapi/kitchen/update', kitchen)
                    .then(function (response) {
                        //show success message or failure
                        if (response.status === 200) {
                            hideAllOptions();
                            $scope.successMessage = true;
                            $scope.currentKitchen.seating_capacity = $scope.kitchenEditCap;
                        } else {
                            hideAllOptions();
                            $scope.nonSuccessMessage = true;
                        }
                    });
            }
        };

        //return functions so they are available to the app
        return exports;

        /**
         * This will display the 2 forms to search for
         * a client by either last name or client id
         */
        function checkClientInForms() {
            //clear the form
            cleanUp();
            //show client search inputs
            $scope.clientSearchInputForm = true;

        }

        /**
         * This will search for clients by id_nbr value.
         * It will show a maximum of 5 values
         */
        function searchClientByID() {
            hideAllOptions();

            //Show the resources loading view
            $scope.loadingResources = true;

            //get user input from form
            var clientId = $scope.clientID;

            //if they don't chose a client, start over
            if (clientId === null || clientId === '' || typeof(clientId) === 'undefined') {
                checkClientInForms();
                return;
            }

            //array to store clients
            $scope.clients = [];

            $http.get('webapi/client/searchID/' + clientId)
                .then(function (response) {
                    for (var client in response.data['clients']) {
                        //loop through the response data and make clients
                        $scope.clients.push(response.data['clients'][client]);
                    }
                    //hide the loading banner
                    $scope.loadingResources = false;

                    //show the clients to choose from
                    $scope.clientList = true;

                }, function () {
                    $scope.loadingResources = false;
                    $scope.kitchenError = true;
                });
        }

        /**
         * Search client by their last name
         */
        function searchClientByName() {
            hideAllOptions();

            //get user input form form
            var clientName = $scope.clientLastName;

            //place to store clients
            $scope.clients = [];

            //call api to search on client name
            $http.get('webapi/client/searchName/' + clientName)
                .then(function (response) {
                    //loop through the response data and make clients
                    for (var client in response.data['clients']) {
                        $scope.clients.push(response.data['clients'][client]);
                    }
                    //hide the loading banner
                    $scope.loadingResources = false;
                    //show the clients to choose from
                    $scope.clientList = true;

                }, function () {
                    $scope.loadingResources = false;
                    $scope.kitchenError = true;
                });
        }

        /**
         * function to display the services used form
         */
        function serviceLogUpdate(){
            hideAllOptions();

            $scope.newServicesUsedReport = true;


        }

        /**
         * We will be submitting text that will be used to create
         * a description in a services used record
         */
        function submitServicesUsed(){
            //hide everything in the shelter section
            hideAllOptions();

            $scope.loadingResources = true;

            var servicesUsed = {
                tracking_client: $scope.checkInClientID,
                description: $scope.serviceUsedReport,
                date_time: null,
                site_number: currSite
            };

            var finalVar = angular.toJson(servicesUsed);

            // submit a post request to the services used api
            $http.post('webapi/client/serviceLog', finalVar)
                .then(function(){

                    //show screen to input information for service used log
                    $scope.loadingResources = false;

                    //Show popup to fill out a services used report
                    $scope.clientCheckInSuccess = true;

                }, function(){
                    $scope.loadingResources = false;
                    $scope.kitchenError = true;
                });
        }

        // clean up scope
        function cleanUp(){
            $scope.clientID = null;
            $scope.clients = null;
            $scope.clientLastName = null;
            $scope.checkInClientID = null;
            $scope.serviceUsedReport = null;

            hideAllOptions();

        }
    }//end of Kitchen Controller

})();