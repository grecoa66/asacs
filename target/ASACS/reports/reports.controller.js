(function(){
    'use strict';
    angular.module('asacs')
        .controller('reportsController', ReportsController);

    ReportsController.$inject = ['$scope', '$http'];

    function ReportsController($scope, $http){

        $scope.bedsReport;

        $scope.mealsRemainData = false;
        $scope.availBedsData = false;
        $scope.noBedsAvail = false;
        $scope.mealsError = false;

        //used when waiting for api call
        $scope.loadingResources = false;

        //add all functions that need to be public
        var exports = {
            hideAllOptions: hideAllOptions,
            showOption: showOption,
            getMealsRemain: getMealsRemain,
            getAvailBeds: getAvailBeds
        };

        /////////////////////////////

        /**
         * Take in what section we want to show.
         * Hide all other sections.
         */
        function showOption(option){
            //first hide all options
            hideAllOptions();

            //next set the correct div var to true
            switch(option){
                case("mealsRemainData"):
                    $scope.mealsRemainData = true;
                    break;
                case("availBedsData"):
                    $scope.availBedsData = true;
                    break;
                case("noBedsAvail"):
                    $scope.noBedsAvail = true;
                    break;
                case("mealsError"):
                    $scope.mealsError = true;
                    break;
                case("loadingResources"):
                    $scope.loadingResources = true;
                    break;
                default:
                    break;
            }
        };

        /**
         * Hide all the sections in the
         * body section of the page
         */
        function hideAllOptions(){
            $scope.mealsRemainData = false;
            $scope.availBedsData = false;
            $scope.noBedsAvail = false;
            $scope.mealsError = false;
            $scope.loadingResources = false;
        };


        /**
         * function to call the reports api.
         * A json object will be returned in
         * the response.data obj.
         */
        function getMealsRemain(){
            hideAllOptions();
            //show loading banner
            $scope.loadingResources = true;

            $http.get("webapi/reports/mealsRemaining")
                .then( function(response){
                    //turn loading banner off
                    $scope.loadingResources = false;

                    //console.log(response.data);
                    if (response.data['meals'] == null)
                    {
                        $scope.mealsError = true;
                    } else {
                        $scope.mealsRemainData = true;
                        $scope.mealsRemaining = response.data['meals'];
                        $scope.mostNeeded = response.data['most_needed'];
                    }
                });

        };

        /**
         * function to call the reports api.
         * A json object will be returned in
         * the response.data obj.
         */
        function getAvailBeds(){
            hideAllOptions();
            //show loading banner
            $scope.loadingResources = true;

            $http.get("webapi/reports/availableBeds")
                .then( function(response){
                    //turn loading banner off
                    $scope.loadingResources = false;

                    //console.log(response.data);
                    if (response.data['beds'] == null)
                    {
                        $scope.noBedsAvail = true;
                    } else {
                        $scope.availBedsData = true;
                        var sites = [];
                        for(var site in response.data['beds']) {
                            sites.push(response.data['beds'][site]);
                        }
                        $scope.bedsReport = sites;
                    }
                });
        };

        //return functions so they are available to the app
        return exports;
    };

})();