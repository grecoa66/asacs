(function(){
    'use strict';

    angular.module('asacs')
        .controller('clientController', ClientController);

    ClientController.$injector = ['$scope', '$http', '$route', 'authService'];

    function ClientController($scope, $http, $route, authService){

        //Check if add Client allowed
        $scope.canAddClient = false;
        $scope.canSearchClient = false;
        //for success modal
        $scope.successMessage = false;

        //Used to check search parameter
        $scope.paramSelect = null;

        //view variables
        $scope.idSearch = false;
        $scope.nameSearch = false;
        $scope.resRow0 = false;
        $scope.resRow1 = false;
        $scope.resRow2 = false;
        $scope.resRow3 = false;
        $scope.resRow4 = false;
        $scope.tooManyResults = false;
        $scope.canViewResults = false;
        $scope.noResults = false;
        $scope.isANameSearch = false;

        var currSite = authService.getUserSite();
        //audit variable
        var user_name = authService.getUserAuthName();


        var exports = {
            addNewClient: addNewClient,
            searchEditClient: searchEditClient,
            searchClientParameter: searchClientParameter,
            processClient: processClient,
            processIdSearch: processIdSearch,
            processNameSearch: processNameSearch,
            hideAllOptions: hideAllOptions,
            editResult: editResult,
            cancelResEdit:  cancelResEdit,
            refineSearch: refineSearch,
            updateClient: updateClient,
            setEditTable: setEditTable,
            submitClientLog: submitClientLog,
            submitNewClientLog: submitNewClientLog,
            searchNewClientForLog: searchNewClientForLog,
            addToWaitlist: addToWaitlist,
            submitServicesUsed: submitServicesUsed,
            showReport : showReport
        };

        //First called when user clicks on add new client menu items
        //perform any preparatory tasks here
        function addNewClient()
        {
            hideAllOptions();
            $scope.newClientForm = null;
            $scope.canAddClient = true;
        }

        //New client form ready
        //Save new client info to DB
        function processClient()
        {

            //build client object we will pass to the back end
            var client = {
                idNbr: $scope.idNbr,
                idDesc: $scope.idDesc,
                firstName: $scope.firstName,
                lastName: $scope.lastName,
                idPhone: $scope.idPhone
            };

            //convert js obj to json
            var finalVar = angular.toJson(client);

            $http.post('webapi/client/add', finalVar)
                .then(function(response){

                    //console.log(response.status.toString());

                    //show success message or failure
                    if(response.status === 200){

                        //audit log code
                        searchNewClientForLog(client);
                        //console.log(response.data)
                        //clear all form elements
                        $scope.idNbr = null;
                        $scope.idDesc = null;
                        $scope.firstName = null;
                        $scope.lastName = null;
                        $scope.idPhone = null;
                        $scope.newClientForm = null;

                        hideAllOptions();

                        $scope.canAddClient = false;
                        $scope.successMessage = true;
                        $route.reload;
                        //console.log("Reload of routing complete");

                    }else{
                        hideAllOptions();
                        $scope.canAddClient = false;
                        $scope.nonSuccessMessage = true;
                        $route.reload;
                    }
                });


        }

        function addNewClient()
        {
            hideAllOptions();
            $scope.canAddClient = true;
        }

        function searchEditClient()
        {
            hideAllOptions();
            $scope.canSearchClient = true;
        }

        /*takes user selected search parameter from the front end
          and calls the api */

        function searchClientParameter(){

            if($scope.paramSelect === null){
                return;
            }else{
                switch ($scope.paramSelect) {
                    case("byId"):
                        $scope.canSearchClient = false;
                        $scope.idSearch = true;
                        break;
                    case("byName"):
                        //hide the selection box and show the search view
                        $scope.canSearchClient = false;
                        $scope.nameSearch = true;
                        break;
                    default:
                        break;
                }
                $scope.paramSelect = null;
            }
        }

        function processIdSearch()
        {
            $scope.canSearchClient = false;
            $scope.idSearch = false;
            $scope.nameSearch = false;
            $scope.isANameSearch = false;

            var poststring = "webapi/client/searchID/" + $scope.searchId;

            $http.get(poststring)
                .then(function(response){

                    //No results returned
                    if (response.data['clients'] === undefined)
                    {
                        $scope.noResults = true;
                        return;
                    }

                    $scope.clientArr = [];

                    //loop through the response data and make clients
                    for(var client in response.data['clients']){
                        $scope.clientArr.push(response.data['clients'][client]);
                        //console.log(response.data['clients'][client]);
                    }
                    if (response.data['clients'].length > 5)
                    {
                        $scope.tooManyResults = true;
                    }
                    else
                    {
                        $scope.canViewResults = true;
                    }
                });
            $scope.searchId = null;
        }

        //De-activate client search results and allow editing
        function editResult()
        {
            $scope.canViewResults = false;
            setEditTable();
        }

        //called to  populate Client edit form with Client data
        function setEditTable()
        {
            $scope.editClientId = $scope.clientIdResult.client_id;
            $scope.editIdNbr = $scope.clientIdResult.id_nbr;
            $scope.editIdDesc = $scope.clientIdResult.id_desc;
            $scope.editFirstName = $scope.clientIdResult.first_name;
            $scope.editLastName = $scope.clientIdResult.last_name;
            $scope.editIdPhone = $scope.clientIdResult.phone_number;

            //activate view
            $scope.canEditClient = true;
        }

        function updateClient()
        {

            var clientLogDescription = "";
            //build client object we will pass to the back end
            var client = {
                clientId: $scope.editClientId,
                idNbr: $scope.editIdNbr,
                idDesc: $scope.editIdDesc,
                firstName: $scope.editFirstName,
                lastName: $scope.editLastName,
                idPhone: $scope.editIdPhone
            };

            clientLogDescription = "User " + user_name + " updated client record: "
                + $scope.clientIdResult.id_nbr + ' ' + $scope.clientIdResult.id_desc + ' ' +  $scope.clientIdResult.first_name
                + ' ' + $scope.clientIdResult.last_name + ' ' + $scope.clientIdResult.phone_number + ' ';

            //Call the client update in the api
            $http.put('webapi/client/update', client)
                .then(function(response){

                    //Handle the response
                    if(response.status === 200){
                        //record in audit log
                        submitClientLog(clientLogDescription, client);
                        //hide everything
                        hideAllOptions();
                        //show the success message
                        $scope.successMessage = true;
                    }else{
                        hideAllOptions();
                        $scope.nonSuccessMessage = true;
                    }


                });

            //clear all edit fields
            $scope.editClientId = null;
            $scope.editIdNbr = null;
            $scope.editIdDesc = null;
            $scope.editFirstName = null;
            $scope.editLastName = null;
            $scope.editIdPhone = null;
        }

        function cancelResEdit()
        {
            hideAllOptions();
            //$route.reload();
        }
        
        function refineSearch()
        {
            hideAllOptions();
            $scope.paramSelect = null;
            $scope.searchId = null;
            $scope.searchLastName = null;

            $scope.tooManyResults = false;
            $scope.canSearchClient = true;
            
        }

        function processNameSearch()
        {
            $scope.isANameSearch = true;
            $scope.canSearchClient = false;
            $scope.nameSearch = false;
            $scope.idSearch = false;

            var poststring = "webapi/client/searchNameMgmt/" + $scope.searchLastName;

            $http.get(poststring)
                .then(function(response){

                    //No results returned
                    if (response.data['clients'] === undefined)
                    {
                        $scope.noResults = true;
                        return;
                    }

                    $scope.clientArr = [];


                    //loop through the response data and make clients
                    for(var client in response.data['clients']){
                        $scope.clientArr.push(response.data['clients'][client]);
                        //console.log(response.data['clients'][client]);
                    }
                    if (response.data['clients'].length > 5)
                    {
                        $scope.tooManyResults = true;
                    }
                    else
                    {
                        $scope.canViewResults = true;
                    }
                });
            $scope.searchLastName = null;
        }

        /**
         * We will be submitting text that will be used to create
         * an audit log entry
         */
        function submitClientLog(logdesc, clnt){
            //hide everything in the shelter section
            hideAllOptions();

            var client_tracked;
            client_tracked = clnt.clientId;

            var auditlogged = {
                tracking_client: client_tracked,
                change_description: logdesc,
                date_time: null
            };

            var finalVar = angular.toJson(auditlogged);

            // submit a post request to the services used api
            $http.post('webapi/client/auditLog', finalVar)
                .then(function(response){

                    //Log generated - add code to inform user
                });
        }

        /**
         * Create audit log for newly added client
         */
        function searchNewClientForLog(in_clnt){
            //hide everything in the client section
            hideAllOptions();

            //build client object we will pass to the back end
            var clnt = {
                idNbr: in_clnt.idNbr,
                idDesc: in_clnt.idDesc,
                firstName: in_clnt.firstName,
                lastName: in_clnt.lastName,
                idPhone: in_clnt.idPhone
            };

            //convert js obj to json
            var finalVar = angular.toJson(clnt);

            //console.log(finalVar);

            //Use to store record returned by search for newly added client
            var newClientArr = [];

            $http.post('webapi/client/searchForAudit', finalVar)
                .then(function(response){

                    //No results returned
                    if (response.data['clients'] === undefined)
                    {
                        console.log("Error: audit module was unable to look up newly added client");
                        return;
                    }

                    //more than one record found
                    if (response.data['clients'].length > 1)
                    {
                        console.log("Error: audit module look up returned more than one record. Unable to log this client add");
                        return;
                    }
                    newClientArr.push(response.data['clients'][0]);
                    //console.log(newClientArr[0]);

                    var logdesc = "User " + user_name + " added a new client record: "
                        + newClientArr[0].id_nbr + ' ' + newClientArr[0].id_desc + ' ' +  newClientArr[0].first_name
                        + ' ' + newClientArr[0].last_name + ' ' + newClientArr[0].phone_number + ' ';

                    var auditlogged = {
                        tracking_client: newClientArr[0].client_id,
                        change_description: logdesc,
                        date_time: null
                    };

                    var finalVar = angular.toJson(auditlogged);

                    // submit a post request to the services used api
                    $http.post('webapi/client/auditLog', finalVar)
                        .then(function(response){

                            //Log generated - add code to inform user
                        });

                });
        }

        function submitNewClientLog()
        {


        }
        /**
         * This function will gather all of the data for the client report
         */
        function showReport(){
            //hide everything
            hideAllOptions();

            //show loading resources banner
            $scope.loadingResources = true;

            console.log($scope.editClientId);
            //array for services
            $scope.serviceLog = [];
            // get the clients services_used log
            $http.get('webapi/client/serviceLog/'+ $scope.editClientId)
                .then(function(response){

                    for(var entry in response.data['service_log']){
                        $scope.serviceLog.push(response.data['service_log'][entry]);
                    }

                }, function(){
                   //something bad happened
                    $scope.loadingResources = false;
                    $scope.clientPageError = true;

                })
                .then(function() {

                    $scope.auditLog = [];

                    $http.get('webapi/client/auditLog/'+ $scope.editClientId)
                        .then(function(response){

                            for(var entry in response.data['audit_log']){
                                $scope.auditLog.push(response.data['audit_log'][entry]);
                            }

                        }, function(){
                            //something bad happened
                            $scope.loadingResources = false;
                            $scope.clientPageError = true;
                        });
                })
                .then(function(){

                    $scope.waitListEntries = [];

                    $http.get('webapi/waitlist/client/'+ $scope.editClientId)
                        .then(function(response){

                            for(var entry in response.data['waitList']){
                                $scope.waitListEntries.push(response.data['waitList'][entry]);
                            }

                        }, function(){
                            //something bad happened
                            $scope.loadingResources = false;
                            $scope.clientPageError = true;
                        });


                })
                .then(function(){
                    $http.get('webapi/shelter/' + currSite)
                        .then(function(response){
                            if(angular.equals({},response.data)){
                                $scope.shelterExists = false;
                            }else{
                                //don't disable all the tabs
                                $scope.shelterExists = true;
                            }


                        }, function(){
                            $scope.loadingResources = false;
                            $scope.clientPageError = true;
                        });
                })
                .then(function(){
                    hideAllOptions();
                    //show the table
                    $scope.clientReport = true;
                });
        }

        /**
         * function to add the current client to the waitlist of the
         * current shelter
         */
        function addToWaitlist(){
            hideAllOptions();
            //show the laoding banner
            $scope.loadingResources = true;

            //create a waitlist obj
            var waitEntry = {
                parent_shelter : currSite,
                client_id : $scope.editClientId
            };

            var finalVar = angular.toJson(waitEntry);

            $http.post('webapi/waitlist', finalVar)
                .then(function(){

                        //show screen to input information for service used log
                        $scope.loadingResources = false;

                        //show success banner
                        $scope.successMessage = true;
                    },
                    function(){
                        $scope.loadingResources = false;
                        $scope.clientAlreadyInWaitList = true;
                    });

        }

        function submitServicesUsed(){

            hideAllOptions();

            $scope.loadingResources = true;

            var servicesUsed = {
                tracking_client: $scope.editClientId,
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

                    //show success banner
                    $scope.successMessage = true;

                }, function(){
                    $scope.loadingResources = false;
                    $scope.clientPageError = true;
                });
        }


        function hideAllOptions(){
            //variables for div selection
            $scope.canAddClient = false;
            //status banners
            $scope.successMessage = false;
            $scope.nonSuccessMessage = false;
            //Search and Edit client variables
            $scope.tooManyResults = false;
            $scope.canViewResults = false;
            $scope.canSearchClient = false;
            $scope.idSearch = false;
            $scope.nameSearch = false;
            $scope.isANameSearch = false;
            $scope.canViewResults = false;
            $scope.canEditClient = false;
            $scope.noResults = false;
            $scope.clientReport = false;
            $scope.clientPageError = false;
            $scope.clientAlreadyInWaitList = false;
        }

        return exports;
    };
})();