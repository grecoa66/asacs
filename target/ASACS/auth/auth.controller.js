/**
 * Created by alexgreco on 3/28/17.
 */
(function(){
    'use strict';

    angular.module('asacs')
        .controller('authController', AuthController);

    AuthController.$inject = ['$http', '$scope', 'authService', '$location'];
    function AuthController($http, $scope, authService, $location){

        $scope.userName;
        $scope.password;

        //for login inputs and button
        $scope.loginPortal = true;
        //for 'checking credentials' banner
        $scope.loggingInBanner = false;
        //for failed login banner
        $scope.loginFailed = false;
        //to show webreports
        $scope.webReports = true;

        $scope.userName = "";
        $scope.siteManaging = "";

        var exports = {
            login : login,
            logout : logout
        };

        //////////////////////////////////////////////////////////
        function hideAll(){
            $scope.loggingInBanner = false;
            $scope.loginPortal = false;
            $scope.loginFailed = false;
            $scope.webReports = false;
        }

        /**
         * basic authentication api call. Takes username and password
         * values from inputs on the auth.html page. Calls the
         * Authentication service on the java side.
         */
        function login(){
            hideAll();
            $scope.loggingInBanner = true;
            if($scope.userName && $scope.password){
                var user = {
                    user_name: $scope.userName,
                    user_password: $scope.password
                }

                var finalVar = angular.toJson(user);

                $http.post('webapi/authenticate', finalVar)
                    .then(function(response){
                        if(response.data === null || response.data === ''){

                            hideAll();
                            //clear values from input fields
                            $scope.userName = null;
                            $scope.password = null;
                            //show warning message and present the login window again
                            $scope.loginFailed = true;
                            $scope.loginPortal = true;
                        }else{
                            var authUser = {
                                first_name : response.data['first_name'],
                                last_name : response.data['last_name'],
                                site_managing : response.data['site_managing'],
                                user_name : response.data['user_name']
                            };

                            authService.setUser(authUser);
                            $scope.userName = authUser.user_name;
                            $scope.siteManaging = authUser.site_managing;
                            document.getElementById("userNameSite").innerHTML = "&nbsp;" + $scope.userName + "&nbsp; : &nbsp; site" + $scope.siteManaging;

                            $location.path('/home');
                        }

                    })
            }
        }//end login

        function logout(){
            if(authService.isUserLoggedIn()) {
                authService.logout();
            }
            document.getElementById("userNameSite").innerHTML = "&nbsp;&nbsp;";
            return;
        }


        return exports;
    }
})();