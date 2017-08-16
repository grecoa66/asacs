/**
 * Created by alexgreco on 3/2/17.
 *
 * @type {angular.Module}
 */
(function(){
'use strict';

    var app = angular.module('asacs',['ngRoute', 'ngResource']);

    app.config(function ($routeProvider, $locationProvider){


        $routeProvider
            .when('/login', {
                templateUrl: 'auth/auth.html'
            })
            .when('/home', {
                templateUrl: 'main/mainMenu.html',
                resolve:{
                    message: function(authService, $location){
                        if( ! authService.isUserLoggedIn() ){
                            $location.path('/login');
                        }
                    }
                }
            })
            .when('/foodbank', {
                templateUrl: 'foodbank/foodbank.html',
                resolve:{
                    message: function(authService, $location){
                        if( ! authService.isUserLoggedIn() ){
                            $location.path('/login');
                        }
                    }
                }
            })
            .when('/services', {
                templateUrl: 'manage_services/services.html',
                resolve:{
                    message: function(authService, $location){
                        if( ! authService.isUserLoggedIn() ){
                            $location.path('/login');
                        }
                    }
                }
            })
            .when('/kitchen', {
                templateUrl:'kitchen/kitchen.html',
                resolve:{
                    message: function(authService, $location){
                        if( ! authService.isUserLoggedIn() ){
                            $location.path('/login');
                        }
                    }
                }
            })
            .when('/pantry', {
                templateUrl: 'pantry/pantry.html',
                resolve:{
                    message: function(authService, $location){
                        if( ! authService.isUserLoggedIn() ){
                            $location.path('/login');
                        }
                    }
                }
            })
            .when('/shelter', {
                templateUrl: 'shelter/shelter.html',
                resolve:{
                    message: function(authService, $location){
                        if( ! authService.isUserLoggedIn() ){
                            $location.path('/login');
                        }
                    }
                }
            })
            .when('/client', {
                templateUrl: 'client/client.html',
                resolve:{
                    message: function(authService, $location){
                        if( ! authService.isUserLoggedIn() ){
                            $location.path('/login');
                        }
                    }
                }
            })
            .when('/reports', {
                templateUrl: 'reports/reports.html'
            })
            .otherwise({
                redirectTo: '/login'
            });

        $locationProvider.html5Mode(true);

    });
})();
