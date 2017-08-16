/**
 * Created by shiemstra3 on 4/3/17.
 * From http://jsfiddle.net/Blackhole/Vq65z/14/ - date directive for angular.js
 */
(function() {
    'use strict';
    angular.module('asacs', [])
        .controller('dateService', function ($scope, dateFilter) {
            $scope.date = new Date();
        })

        .directive(
            'dateInput',
            function (dateFilter) {
                return {
                    require: 'ngModel',
                    template: '<input type="date"></input>',
                    replace: true,
                    link: function (scope, elm, attrs, ngModelCtrl) {
                        ngModelCtrl.$formatters.unshift(function (modelValue) {
                            return dateFilter(modelValue, 'yyyy-MM-dd');
                        });

                        ngModelCtrl.$parsers.unshift(function (viewValue) {
                            return new Date(viewValue);
                        });
                    },
                };
            });
})();