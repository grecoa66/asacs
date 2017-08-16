/**
 * Created by alexgreco on 3/31/17.
 */
(function(){
    angular.module('asacs')
        .service('shelterService', ShelterService);

    ShelterService.$inject = ['$http']

    function ShelterService($http){


        var exports = {
            clientSearchName: clientSearchName,
            clientSearchID: clientSearchID
        };
        /////////////////////////////////////////////

        function clientSearchName(inputText){
            $http.get('webapi/client/searchName/' + inputText)
                .then(function(response){

                    var clients = [];

                    for(var client in response.data["clients"]){
                        console.log(client.first_name);
                        client.push(client);
                    }
                    return clients;
                });
        }

        function clientSearchID(inputID){

            $http.get('webapi/client/searchID/{id}', inputID)
                .then(function(response){
                    var user = response.data[client]
                    console.log()
                    return user;
                });
        }

        return exports;
    }
})();