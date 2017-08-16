/**
 * Created by alexgreco on 3/10/17.
 */

(function(){
    'use strict';
    angular.module('asacs')
        .controller('pantryController', PantryController);

    PantryController.$inject = ['$scope', '$http', 'authService'];
    function PantryController($scope, $http, authService){
        var currSite = authService.getUserSite();

        var exports = {
            doesExist: doesExist,
            hideAllElements : hideAllElements,
            searchClientByID : searchClientByID,
            searchClientByName : searchClientByName,
            checkClientInForms : checkClientInForms,
            serviceLogUpdate : serviceLogUpdate,
            submitServicesUsed : submitServicesUsed,
            cleanUp : cleanUp
        };

        //////////////////////////////////////////////////////

        function hideAllElements(){
            $scope.loadingResources = false;
            $scope.clientCheckInSuccess = false;
            $scope.pantryError = false;
            $scope.clientSearchInputForm = false;
            $scope.clientList = false;
            $scope.newServicesUsedReport = false;
        }

        //If there is no pantry for this site, disable the menu
        function doesExist(){
            hideAllElements();

            //show the loading banner
            $scope.loadingResources = true;

            $http.get('webapi/pantry/'+currSite)
                .then(function(response){
                    //if no pantry exists, disable the menu options
                    if(response.data['parent_site'] == null){
                        $scope.disableMenu = true;
                    }

                }, function(){
                    $scope.loadingResources = false;
                    $scope.pantryError = true;
                });
        }

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
            hideAllElements();

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
                    $scope.pantryError = true;
                });
        }

        /**
         * Search client by their last name
         */
        function searchClientByName() {
            hideAllElements();

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
                    $scope.pantryError = true;
                });
        }

        /**
         * function to display the services used form
         */
        function serviceLogUpdate(){
            hideAllElements();

            $scope.newServicesUsedReport = true;
        }

        /**
         * We will be submitting text that will be used to create
         * a description in a services used record
         */
        function submitServicesUsed(){
            //hide everything in the shelter section
            hideAllElements();

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
                    $scope.pantryError = true;
                });
        }

        /**
         * Call this function to clear the page of all elements
         * and clear the scope for the page.
         */
        function cleanUp(){
            $scope.clientID = null;
            $scope.clients = null;
            $scope.clientLastName = null;
            $scope.checkInClientID = null;
            $scope.serviceUsedReport = null;

            hideAllElements();
        }

        return exports;
    }
})();