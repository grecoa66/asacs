/**
 * Created by alexgreco on 3/28/17.
 */
(function(){
    'use strict';

    angular.module('asacs')
        .service('authService', AuthService);
    function AuthService(){

        var currUserName = null;
        var currUserSite = null;
        var currAuthName = null;

        var exports = {
            setUser: setUser,
            isUserLoggedIn : isUserLoggedIn,
            getUserSite: getUserSite,
            logout: logout,
            getUserName: getUserName,
            getUserAuthName: getUserAuthName
        };

        /**
         * This function will be called from the auth.controller.js
         * after it receives a successful response from the authenticate
         * api call.
         * @param user
         */
        function setUser(user){

            currUserName = user.first_name + ' ' + user.last_name;
            currUserSite = user.site_managing;
            currAuthName = user.user_name;
        }

        function getUserName(){
            if(currUserName !== null) {
                return currUserName;
            }
        }

        function getUserAuthName(){
            if(currAuthName !== null) {
                return currAuthName;
            }
        }

        /**
         * Used to determine if the user is currently logged in.
         * This will be used in the app.js file to determine
         * if a user can go to the page they selected or if they
         * should be redirected to the login page
         * @returns {boolean}
         */
        function isUserLoggedIn(){
            if(currUserName === null){
                return false;
            }else{
                return true;
            }
        }

        /**
         * This will be called in the various controllers of the
         * frontend to determine what the current site it. This
         * will be used a lot to make sure the user is updating the
         * correct site and services
         * @returns {*}
         */
        function getUserSite(){
            return currUserSite;
        }

        /**
         * delete the data of the current user, effectivly logging them out.
         */
        function logout(){
            currUserName = null;
            currUserSite = null;
            document.getElementById("userNameSite").innerHTML = "&nbsp;&nbsp;";
        }

        return exports;
    }
})();