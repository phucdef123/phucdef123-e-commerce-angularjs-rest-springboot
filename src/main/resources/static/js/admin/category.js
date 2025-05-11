(function() {
    var app = angular.module('app');

    app.controller('CategoryController', function($scope, $rootScope, $http, PaginationService, $timeout) {
        $scope.items = [];
        $scope.form = {};
        $scope.sortColumn = "id";
        $scope.reverse = false;// false = tăng dần
        console.log($scope.action);
        var host = "http://localhost:8080/admin/categories";

        $scope.create = function () {
            var item = angular.copy($scope.form);
            item.id = null;
            $http.post(host, item).then(function (response) {
                $scope.items.push(response.data);
                $scope.reset();
                console.log(response);
                Swal.fire({
                    icon: 'success',
                    title: 'Thêm mới thành công!',
                    showConfirmButton: false,
                    timer: 1000
                });
            }).catch(function (error) {
                console.log(error);
                Swal.fire({
                    icon: 'error',
                    title: 'Thêm mới thất bại!',
                    text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                });
            })
        }
        $scope.update = function () {
            var item = angular.copy($scope.form);
            var url = `${host}/${$scope.form.id}`;
            $http.put(url, item).then(response => {
                var index = $scope.items.findIndex(item => item.id === $scope.form.id);
                $scope.items[index] = response.data;
                console.log(response.data);
                Swal.fire({
                    icon: 'success',
                    title: 'Cập nhật thành công!',
                    showConfirmButton: false,
                    timer: 1000
                });
            }).catch(error => {
                console.log(error);
                Swal.fire({
                    icon: 'error',
                    title: 'Cập nhật thất bại!',
                    text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                });
            })
        }
        $scope.delete = function (key) {
            var url = `${host}/${key}`;
            $http.delete(url).then(response => {
                var index = $scope.items.findIndex(item => item.id === key);
                $scope.items.splice(index, 1);
                $scope.reset();
                console.log(response.data);
                Swal.fire({
                    icon: 'success',
                    title: 'Xóa thành công!',
                    showConfirmButton: false,
                    timer: 1000
                });
            }).catch(error => {
                console.log(error);
                Swal.fire({
                    icon: 'error',
                    title: 'Xóa thất bại!',
                    text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                });
            })
        }
        $scope.loadAll = function () {
            $http.get(host).then(response => {
                $scope.items = response.data;
                $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
                console.log(response.data);
            }).catch(error => {
                console.log(error);
            });
        };
        $scope.edit = function (key) {
            var url = `${host}/${key}`;
            $http.get(url).then(response => {
                $scope.form = response.data;
                console.log(response);
            }).catch(error => {
                console.log(error);
            })
        }
        $scope.reset = function () {
            $scope.form = {};
            $scope.key = null;
        }


        $scope.pager = PaginationService.createPager(8);

        $scope.sortBy = function (column) {
            if ($scope.sortColumn === column) {
                $scope.reverse = !$scope.reverse; // Đảo thứ tự nếu cùng cột
            } else {
                $scope.sortColumn = column;
                $scope.reverse = false; // Mặc định tăng dần khi đổi cột
            }
            $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
        };
        $rootScope.$on('searchResults/categories', function(event, items) {
            $timeout(function() {
                $scope.items = items.length ? items : [];
                console.log("Dữ liệu sau khi cập nhật:", $scope.items);
                $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
            });

        });

        $scope.reset();
        $scope.loadAll();
    });
})();