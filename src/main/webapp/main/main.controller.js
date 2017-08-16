/**
 * This controller will control the main menu
 * of the application. It will control the
 * mainMenu.html page.
 * Created by alexgreco on 3/2/17.
 */
(function(){
    'use strict';
    angular.module('asacs')
        .controller('mainController', MainController);

    MainController.$injector = ['$scope'];
    function MainController($scope){
        $scope.loginUserName = null;
        $scope.loginPassword = null;

        /**
         * add all functions that need to be public
         * and available to the view (html page)
         */
        var exports = {
            login: login
        }

        /**
         * This function will take care of the login functionality
         */
        function login(){

        }

        return exports;
    }
})();