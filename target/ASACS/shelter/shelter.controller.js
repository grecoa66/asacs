/**
 * Created by alexgreco on 3/30/17.
 */
(function(){

    angular.module('asacs')
        .controller('shelterController', ShelterController);

    ShelterController.$inject = ['$scope', '$http', 'authService'];

    function ShelterController($scope, $http, authService){
        var currSite = authService.getUserSite();

        //adjusts flow for check in or check out
        $scope.flowOption;

        $scope.loadingResources = false;

        var exports = {
            hideAllElements: hideAllElements,
            checkForShelter: checkForShelter,
            checkClientInForms: checkClientInForms,
            searchClientByID: searchClientByID,
            searchClientByName: searchClientByName,
            checkClientServiceLog : checkClientServiceLog,
            clientCheckInPref: clientCheckInPref,
            showAvailability: showAvailability,
            addClientToWaitlist: addClientToWaitlist,
            removeClientWaitList: removeClientWaitList,
            editWaitlist : editWaitlist,
            submitServicesUsed: submitServicesUsed,
            checkClientIn: checkClientIn,
            setMenuOption: setMenuOption,
            moveClientWaitList: moveClientWaitList,
            viewRooms: viewRooms,
            editRooms: editRooms,
            editRoomForm: editRoomForm,
            submitRoomUpdate: submitRoomUpdate,
            viewWaitList: viewWaitList,
            getClientInWaitList : getClientInWaitList,
            viewBunks: viewBunks,
            editBunks: editBunks,
            editBunksForm: editBunksForm,
            submitBunkUpdate: submitBunkUpdate,
            cleanUp: cleanUp
        };
        //////////////////////////////////////////////////////

        /**
         * Use this function to hide all elements of the page
         */
        function hideAllElements(){
            $scope.loadingResources = false;
            $scope.clientSearchInputForm = false;
            $scope.clientList = false;
            $scope.auditLogTable = false;
            $scope.serviceLogTable = false;
            $scope.clientCheckInPref = false;
            $scope.availRoomsTable = false;
            $scope.shelterPageError = false;
            $scope.newServicesUsedReport = false;
            $scope.clientCheckInSuccess = false;
            $scope.noRoomAvailable = false;
            $scope.waitListOption = false;
            $scope.clientCheckInWaitSuccessful = false;
            $scope.clientRemoveWaitSuccessful = false;
            $scope.availBunksTable = false;
            $scope.noBunksAvailable = false;
            $scope.checkInButton = false;
            $scope.checkOutButton = false;
            $scope.waitListAddButton = false;
            $scope.waitListRemoveButton = false;
            $scope.clientEditWaitlistTable = false;
            $scope.successfulCheckOut = false;
            $scope.viewRoomsTable = false;
            $scope.editRoomsTable = false;
            $scope.editRoomForm = false;
            $scope.generalSuccess = false;
            $scope.clientInWaitListError = false;
            $scope.clientsInWaitlist = false;
            $scope.clientNotInWaitListError = false;
            $scope.waitListTable = false;
            $scope.allBunks = false;
            $scope.allBunksEdit = false;
            $scope.editBunkForm = false;
        }

        /**
         * Turn the correct menu option on
         * @param value
         */
        function setMenuOption(value){

            turnActiveOff();

            switch(value){
                case('clientCheckIn'):
                    $scope.clientCheckIn = true;
                    break;
                case('clientCheckOut'):
                    $scope.clientCheckOut = true;
                    break;
                case('viewRoom'):
                    $scope.viewRoom = true;
                    break;
                case('editRoom'):
                    $scope.editRoom = true;
                    break;
                case('viewBunk'):
                    $scope.viewBunk = true;
                    break;
                case('editBunk'):
                    $scope.editBunk = true;
                    break;
                case('viewWaitList'):
                    $scope.viewWaitList = true;
                    break;
                case('editWaitList'):
                    $scope.editWaitList = true;
                    break;
                case('addWaitList'):
                    $scope.addWaitList = true;
                    break;
                case('removeWaitList'):
                    $scope.removeWaitList = true;
                default:
                    break;
            }

        }

        /**
         * Turn the active menu option off
         */
        function turnActiveOff(){
            $scope.clientCheckIn = false;
            $scope.clientCheckOut = false;
            $scope.viewRoom = false;
            $scope.editRoom = false;
            $scope.viewBunk = false;
            $scope.editBunk = false;
            $scope.viewWaitList = false;
            $scope.editWaitList = false;
            $scope.addWaitList = false;
            $scope.removeWaitList = false;
        }


        function checkForShelter(){
            cleanUp();

            //show the loading banner
            $scope.loadingResources = true;

            //call the shelter api and see if a shelter exists for this site
            $http.get('webapi/shelter/' + currSite)
                .then(function(response){
                    if(angular.equals({},response.data)){
                        //disable all the tabs
                        $scope.disableMenu = true;
                    }else{
                        //don't disable all the tabs
                        $scope.disableMenu = false;
                    }
                    // hide the loading resources banner
                    $scope.loadingResources = false;

                }, function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });
        }

        /**
         * This will display the 2 forms to search for
         * a client by either last name or client id
         */
        function checkClientInForms(checkIn){
            //Hide everything on the page
            hideAllElements();
            //show client search inputs
            $scope.clientSearchInputForm = true;

            $scope.flowOption = checkIn;
        }

        /**
         * This will search for clients by id_nbr value.
         * It will show a maximum of 5 values
         */
        function searchClientByID(){
            hideAllElements();

            //Show the resources loading view
            $scope.loadingResources = true;

            //get user input from form
            var clientId = $scope.clientID;

            //if they don't chose a client, start over
            if(clientId === null || clientId === '' || typeof(clientId) === 'undefined'){
                checkClientInForms();
                return;
            }

            //array to store clients
            $scope.clients = [];

            $http.get('webapi/client/searchID/' + clientId)
                .then(function(response){
                    for(var client in response.data['clients']) {
                        //loop through the response data and make clients
                        $scope.clients.push(response.data['clients'][client]);
                    }
                    //hide the loading banner
                    $scope.loadingResources = false;

                    //show button for flow you are on
                    switch($scope.flowOption){
                        case('checkIn'):
                            $scope.checkInButton = true;
                            break;
                        case('checkOut'):
                            $scope.checkOutButton = true;
                            break;
                        case('addWaitList'):
                            $scope.waitListAddButton = true;
                            break;
                        default:
                            break;
                    }

                    //show the clients to choose from
                    $scope.clientList = true;
                }, function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });
        }

        /**
         * Search client by their last name
         */
        function searchClientByName(){
            hideAllElements();

            //get user input form form
            var clientName = $scope.clientLastName;

            //place to store clients
            $scope.clients = [];

            //call api to search on client name
            $http.get('webapi/client/searchName/'+ clientName)
                .then(function(response){
                    //loop through the response data and make clients
                    for(var client in response.data['clients']){
                        $scope.clients.push(response.data['clients'][client]);
                    }
                    //hide the loading banner
                    $scope.loadingResources = false;
                    //show the clients to choose from
                    $scope.clientList = true;

                    //Show button for flow you are on
                    switch($scope.flowOption){
                        case("checkIn"):
                            $scope.checkInButton = true;
                            break;
                        case("checkOut"):
                            $scope.checkOutButton = true;
                            break;
                        case("addWaitList"):
                            $scope.waitListAddButton = true;
                            break;
                        case('removeWaitList'):
                            $scope.waitListRemoveButton = true;
                            break;
                        default:
                            break;
                    }

                }, function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });
        }

        /**
         * This will grab a clients services log and display it on
         * the screen and ensure that the user reviews it before
         * checking a client into a room/bunk/waitlist
         */
        function checkClientServiceLog(){
            hideAllElements();

            $scope.loadingResources = true;

            /*  If the flow is checking a client out
            *   This is where checkout diverges from the checkin flow
            *   We call the checkout method and stop the flow there.
            */
            if($scope.flowOption === 'checkOut'){
                checkClientOut($scope.checkInClientID);
                return;
            }else if($scope.flowOption === 'addWaitList'){
                addClientToWaitlist();
                return;
            }else if($scope.flowOption === 'removeWaitList'){
                removeClientWaitList();
                return;
            }

            //grab the client in questions ID
            var clientId = $scope.checkInClientID;

            //if they don't chose a client, start over
            if(clientId === null || clientId === '' || typeof(clientId) === 'undefined'){
                checkClientInForms();
                return;
            }

            //audit log entries
            $scope.serviceLog = [];

            //call the api for this clients audit log entries
            $http.get("webapi/client/serviceLog/" + clientId)
                .then(function(response){
                    for(var entry in response.data['service_log']){
                        $scope.serviceLog.push(response.data['service_log'][entry]);
                    }
                    //hide loading banner
                    $scope.loadingResources = false;

                    //show table of audit records
                    $scope.serviceLogTable = true;
                }, function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });
        }

        /**
         * Check if the client wants to check into a bunk or a room.
         */
        function clientCheckInPref(){
            hideAllElements();

            $scope.clientCheckInPref = true;
        }

        /**
         * Check the availability of the clients selection in shelter
         * arrangment. After this try to check the client in that room
         */
        function showAvailability(){
            hideAllElements();

            //show the loading banner
            $scope.loadingResources = true;

            //array to hold available rooms
            $scope.availRooms = [];

            //array to hold available bunks
            $scope.availBunks = [];

            if($scope.clientShelterPref === 'room'){

                //check if there are any rooms for this shelter
                $http.get('webapi/rooms/'+currSite)
                    .then(function(response){

                        for(var room in response.data['rooms']){
                            $scope.availRooms.push(response.data['rooms'][room]);
                        }
                        //check if the shelter has any rooms at all
                        if($scope.availRooms.length > 0) {
                            //if they do, check which ones are available

                            //query to get all available rooms
                            $http.get('webapi/rooms/available/' + currSite)
                                .then(function(response){

                                    //clear the array from the for loop above
                                    $scope.availRooms = [];

                                    for(var room in response.data['rooms']){
                                        $scope.availRooms.push(response.data['rooms'][room]);
                                    }

                                    if($scope.availRooms.length > 0) {
                                        //show the avail rooms table
                                        $scope.availRoomsTable = true;
                                    }else{
                                        //else show that there are no rooms
                                        $scope.waitListOption = true;
                                    }

                                    //hide the loading banner
                                    $scope.loadingResources = false;
                                }, function(){
                                    $scope.loadingResources = false;
                                    $scope.shelterPageError = true;
                                });
                        }else{
                            //hide the loading banner
                            $scope.loadingResources = false;
                            $scope.noRoomAvailable = true;
                        }
                    }, function(){
                        $scope.loadingResources = false;
                        $scope.shelterPageError = true;
                    });

            }else if($scope.clientShelterPref === 'bunk'){
                //query to get all available rooms
                $http.get('webapi/bunks/available/' + currSite)
                    .then(function(response){

                        for(var bunk in response.data['bunks']){
                            $scope.availBunks.push(response.data['bunks'][bunk]);
                        }

                        //hide the loading banner
                        $scope.loadingResources = false;

                        if($scope.availBunks.length > 0) {
                            //show the avail rooms table
                            $scope.availBunksTable = true;
                        }else{
                            //else show that there are no rooms
                            $scope.noBunksAvailable = true;
                        }
                    }, function(){
                        $scope.loadingResources = false;
                        $scope.shelterPageError = true;
                    });
            }else{
                checkClientInForms();
                return;
            }
        }

        /**
         * Finally we reach the part of the flow that is actually
         * changing the record in room/bunk table for the current client.
         */
        function checkClientIn(){
            //hide everything in the shelter section
            hideAllElements();

            $scope.loadingResources = true;

            if($scope.clientShelterPref === 'room') {
                var updateRoom = {
                    room_id: $scope.selectClientRoom,
                    parent_shelter: currSite,
                    occupying_client: $scope.checkInClientID,
                    occupied: true
                };

                var finalVar = angular.toJson(updateRoom);

                $http.put('webapi/rooms/update', finalVar)
                    .then(function (response) {
                        //show screen to input information for service used log
                        $scope.loadingResources = false;

                        //Show popup to fill out a services used report
                        $scope.newServicesUsedReport = true;

                    }, function(){
                        $scope.loadingResources = false;
                        $scope.shelterPageError = true;
                    });

            }else if($scope.clientShelterPref === 'bunk'){
                var updateBunk = {
                    bunk_id : $scope.selectClientBunk.bunk_id,
                    parent_shelter: currSite,
                    occupying_client: $scope.checkInClientID,
                    bunk_type: $scope.selectClientBunk.bunk_type,
                    occupied: true
                }

                var finalVar = angular.toJson(updateBunk);

                $http.put('webapi/bunks/update', finalVar)
                    .then(function(response){

                        //show screen to input information for service used log
                        $scope.loadingResources = false;

                        //Show popup to fill out a services used report
                        $scope.newServicesUsedReport = true;

                    }, function(){
                        $scope.loadingResources = false;
                        $scope.shelterPageError = true;
                    });
            }else{
                //failure
                $scope.loadingResources = false;
                $scope.shelterPageError = true;
            }

        }

        /**
         * This function grabs the selected client and checks them out
         * of either a bunk or room. They user will then be passed to the
         * submit services used function to note that the user checked out.
         * @param clientID
         */
        function checkClientOut(clientID){

            $http.put('webapi/client/checkout/' + clientID)
                .then(function(response){

                    //hide loading banner
                    $scope.loadingResources = false;
                    //successful checkout banner
                    $scope.successfulCheckOut = true;
                }, function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });
        }

        /**
         * This function will get the wait-list for the
         * current site and display it in a table
         */
        function viewWaitList(){
            hideAllElements();

            //show loading banner
            $scope.loadingResources = true;

            //array to hold wait list entries
            $scope.waitListEntries = [];

            //query for the waitlist for this site
            $http.get('webapi/waitlist/'+currSite)
                .then(function(response){
                    for(var entry in response.data['waitList']){
                        $scope.waitListEntries.push(response.data['waitList'][entry]);
                    }

                    //hide loading banner
                    $scope.loadingResources = false;

                    //Show the waitList entry table
                    $scope.waitListTable = true;

                }, function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });

        }

        /**
         * Add the current client to a waitlist
         */
        function addClientToWaitlist(){
            hideAllElements();

            var waitEntry = {
                parent_shelter : currSite,
                client_id : $scope.checkInClientID
            };

            var finalVar = angular.toJson(waitEntry);

            $http.post('webapi/waitlist', finalVar)
                .then(function(){

                    //show screen to input information for service used log
                    $scope.loadingResources = false;

                    //Show popup to fill out a services used report
                    $scope.clientCheckInWaitSuccessful = true;
                },
                function(){
                    $scope.loadingResources = false;
                    $scope.clientInWaitListError = true;
                });
        }

        function getClientInWaitList(){
            cleanUp();
            hideAllElements();

            //show loading banner
            $scope.loadingResources = true;

            //all clients in waitlist
            $scope.clientInWaitlist = [];

            $http.get('webapi/waitlist/' + currSite)
                .then(function(response){
                    for(var client in response.data['waitList']){
                        $scope.clientInWaitlist.push(response.data['waitList'][client]);

                    }

                    //show screen to input information for service used log
                    $scope.loadingResources = false;

                    $scope.clientsInWaitlist = true;

                }, function(){
                    $scope.loadingResources = false;
                    $scope.clientInWaitListError = true;
                });
        }

        /**
         * Remove a client form waitlist
         */
        function removeClientWaitList(){
            hideAllElements();

            $http.delete('webapi/waitlist/remove/'+ $scope.checkInClientID)
                .then(function(){

                    //show screen to input information for service used log
                    $scope.loadingResources = false;

                    //Show popup to fill out a services used report
                    $scope.clientRemoveWaitSuccessful = true;
                },
                function(){
                    $scope.loadingResources = false;
                    $scope.clientNotInWaitListError = true;
                });
        }

        /**
         * This function will be used to get the current
         * client in the site's waitlist. This function will
         * populate a table where the user can select which entry
         * in the waitlist they want to edit.
         */
        function editWaitlist(){
            hideAllElements();

            //show loading resources banner
            $scope.loadingResources = true;

            //array of all the clients in the waitlist
            $scope.clientsInEditWaitlist = [];

            //call api to get all clients in waitlist
            $http.get('webapi/waitlist/' + currSite)
                .then(function(response){
                    for(var client in response.data['waitList']){
                        //put each client into the array
                        $scope.clientsInEditWaitlist
                            .push(response.data['waitList'][client]);
                    }

                    //turn off loading banner
                    $scope.loadingResources = false;

                    //show the table that the user can select a client form
                    $scope.clientEditWaitlistTable = true;
                }, function(){
                    $scope.loadingResources = false;
                    $scope.clientNotInWaitListError = true;
                });
        }

        function moveClientWaitList(direction){
            hideAllElements();
            //show the loading banner
            $scope.loadingResources = true;

            if(direction === 'up') {
                //call the api to move the selected client up
                $http.put('webapi/waitlist/move/up/' + $scope.editWaitlistSelect + '/' + currSite)
                    .then(function () {
                        editWaitlist();
                    }, function () {
                        $scope.loadingResources = false;
                        $scope.clientNotInWaitListError = true;
                    });
            }else{
                $http.put('webapi/waitlist/move/down/' + $scope.editWaitlistSelect + '/' + currSite)
                    .then(function () {
                        editWaitlist();
                    }, function () {
                        $scope.loadingResources = false;
                        $scope.clientNotInWaitListError = true;
                    });
            }
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
                .then(function(response){

                    //show screen to input information for service used log
                    $scope.loadingResources = false;

                    cleanUp();

                    //Show popup to fill out a services used report
                    $scope.clientCheckInSuccess = true;

                }, function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });
        }

        /**
         * This function will set up the view to just show the current rooms
         */
        function viewRooms(){

            //make sure scope is clean
            cleanUp();

            //show the loading banner
            $scope.loadingResources = true;

            //array to hold available rooms
            $scope.availRooms = [];

            //query to get all available rooms
            return $http.get('webapi/rooms/' + currSite)
                .then(function(response){

                    for(var room in response.data['rooms']){
                        $scope.availRooms.push(response.data['rooms'][room]);
                    }

                    //hide the loading banner
                    $scope.loadingResources = false;

                    if($scope.availRooms.length > 0) {
                        //show the avail rooms table
                        $scope.viewRoomsTable = true;
                    }else{
                        //else show that there are no rooms
                        $scope.noRoomAvailable = true;
                    }
                }, function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });

        }

        /**
         * This function is very redundent but it grabs all the rooms from a site
         * and shows the room table that gives you the option to select a room
         * to edit.
         * @returns {*}
         */
        function editRooms(){
            //make sure scope is clean
            cleanUp();

            //show the loading banner
            $scope.loadingResources = true;

            //array to hold available rooms
            $scope.availRooms = [];

            //query to get all available rooms
            return $http.get('webapi/rooms/' + currSite)
                .then(function(response){

                    for(var room in response.data['rooms']){
                        $scope.availRooms.push(response.data['rooms'][room]);
                    }

                    //hide the loading banner
                    $scope.loadingResources = false;

                    if($scope.availRooms.length > 0) {
                        //show the avail rooms table
                        $scope.editRoomsTable = true;
                    }else{
                        //else show that there are no rooms
                        $scope.noRoomAvailable = true;
                    }
                }, function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });
        }

        /**
         * Grabs the room you edit and passes the data off to the
         * room update screen.
         */
        function editRoomForm(){
            //Hide all elements on page
            hideAllElements();

            //Show the edit room form
            $scope.editRoomForm = true;

            $scope.selectEditRoom = angular.fromJson($scope.selectEditRoom);

        }

        /**
         * Sends a request to the room api to update whatever the user did or
         * did not change in the room's information
         */
        function submitRoomUpdate(){

            //hide everything in the shelter section
            hideAllElements();

            $scope.loadingResources = true;

            //if no client is entered, put zero and occupied to false
            if($scope.selectEditRoom.occupying_client === null){
                $scope.selectEditRoom.occupying_client = 0;
            }

            //build updated room obj
            var room = {
                room_id: $scope.selectEditRoom.room_id,
                parent_shelter: $scope.selectEditRoom.parent_shelter,
                occupying_client: $scope.selectEditRoom.occupying_client,
                occupied: $scope.selectEditRoom.occupied

            };
            //prepare obj for put request
            var finalVar = angular.toJson(room);

            $http.put('webapi/rooms/update',finalVar)
                .then(function (response) {
                    //show screen to input information for service used log
                    $scope.loadingResources = false;
                    $scope.generalSuccess = true;
                }, function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });
        }

        /**
         * this function will get all the bunks and their
         * information for this shelter/site.
         */
        function viewBunks(){
            hideAllElements();

            //loading banner
            $scope.loadingResources = true;

            //an array for all the bunks
            $scope.allBunksArr = [];

            //call the api for all the bunks for the site
            $http.get('webapi/bunks/'+currSite)
                .then(function(response){
                    for(var bunk in response.data['bunks']){
                        $scope.allBunksArr.push(response.data['bunks'][bunk]);
                    }

                    //hide loading banner
                    $scope.loadingResources = false;

                    if($scope.allBunksArr.length > 0){
                        //show the bunks table
                        $scope.allBunks = true;
                    }else{
                        $scope.noBunksAvailable = true;
                    }

                },
                function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });
        }

        /**
         * this function gets the modified bunk object and
         * calls the bunk api to update the object.
         */
        function editBunks(){
            hideAllElements();

            //loading banner
            $scope.loadingResources = true;

            //an array for all the bunks
            $scope.allBunksArr = [];

            //call the api for all the bunks for the site
            $http.get('webapi/bunks/'+currSite)
                .then(function(response){
                        for(var bunk in response.data['bunks']){
                            $scope.allBunksArr.push(response.data['bunks'][bunk]);
                        }

                        //hide loading banner
                        $scope.loadingResources = false;

                        if($scope.allBunksArr.length > 0){
                            //show the bunks table
                            $scope.allBunksEdit = true;
                        }else{
                            $scope.noBunksAvailable = true;
                        }

                    },
                    function(response){
                        $scope.loadingResources = false;
                        $scope.shelterPageError = true;
                    });
        }

        /**
         * Gets the selected bunk and turns it into a js obj
         * and opens the edit form
         */
        function editBunksForm(){
            hideAllElements();

            $scope.editBunkForm = true;

            $scope.selectEditBunk = angular.fromJson($scope.selectEditBunk);


        }

        /**
         * Gather the inputs from the front end and call the update
         * method in the bunk API
         */
        function submitBunkUpdate(){
            hideAllElements();

            //loading banner
            $scope.loadingResources = true;

            //if no client is entered, put zero and occupied to false
            if($scope.selectEditBunk.occupying_client === null){
                $scope.selectEditBunk.occupying_client = 0;
            }

            //build updated room obj
            var bunk = {
                bunk_id: $scope.selectEditBunk.bunk_id,
                parent_shelter: $scope.selectEditBunk.parent_shelter,
                occupying_client: $scope.selectEditBunk.occupying_client,
                occupied: $scope.selectEditBunk.occupied,
                bunk_type: $scope.selectEditBunk.bunk_type

            };
            //prepare obj for put request
            var finalVar = angular.toJson(bunk);

            $http.put('webapi/bunks/update',finalVar)
                .then(function () {
                    //show screen to input information for service used log
                    $scope.loadingResources = false;
                    $scope.generalSuccess = true;
                },
                function(){
                    $scope.loadingResources = false;
                    $scope.shelterPageError = true;
                });
        }

        /**
         * Use this function when the check in was successful.
         * This will reset all of the values of the scope variable
         * back to empty.
         */
        function cleanUp(){
            $scope.clientShelterPref = null;
            $scope.serviceLog = null;
            $scope.checkInClientID = null;
            $scope.searchByIdClient = null;
            $scope.clients = null;
            $scope.auditLog = null;
            $scope.clientID = null;
            $scope.clientLastName = null;
            $scope.availBunks = null;
            $scope.serviceUsedReport = null;
            $scope.selectEditRoom = null;
            $scope.waitListEntries = null;
            $scope.allBunksArr = null;
            $scope.selectEditBunk = null;
            $scope.clientInWaitlist = null;
            $scope.clientsInEditWaitlist = null;

            hideAllElements();
            turnActiveOff();
        }


        return exports;
    }

})();