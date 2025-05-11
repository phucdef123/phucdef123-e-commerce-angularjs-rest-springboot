(function () {
    var app = angular.module('app');
    app.controller('OrderController', function($scope, $http, $rootScope, $timeout, PaginationService ) {
        $scope.items = [];
        $scope.form = {};
        $scope.sortColumn = "id";
        $scope.reverse = false;// false = tăng dần
        var host = "http://localhost:8080/admin/orders";

        $scope.update = function () {
            var item = angular.copy($scope.form);
            var url = `${host}/${$scope.form.id}`;
            $http.put(url, item).then(response => {
                var index = $scope.items.findIndex(item => item.id === $scope.form.id);
                $scope.items[index] = response.data;
                $scope.loadAll();
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
        $scope.edit = function (key) {
            var url = `${host}/${key}`;
            $http.get(url).then(response => {
                console.log("Dữ liệu từ API:", response.data);
                $scope.form = angular.copy(response.data); // dùng copy để tránh reference bug

                // Lưu trạng thái gốc để dùng cho dropdown
                $scope.originalStatus = response.data.status;
                console.log(url);
            }).catch(error => {
                console.log(error);
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
        $scope.statusOptions = ["PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"];

        $scope.transitionMap = {
            PENDING: ["PROCESSING", "CANCELLED"],
            PROCESSING: ["SHIPPED", "CANCELLED"],
            SHIPPED: ["DELIVERED"],
            DELIVERED: [],
            CANCELLED: []
        };

        $scope.getAllowedStatuses = function (currentStatus) {
            return $scope.transitionMap[currentStatus] || [];
        };
        $scope.getStatusClass = function (status) {
            switch (status) {
                case 'PENDING':
                    return 'bg-warning';
                case 'SHIPPED':
                    return 'bg-info';
                case 'DELIVERED':
                    return 'bg-success';
                case 'CANCELLED':
                    return 'bg-danger';
                default:
                    return 'bg-dark';
            }
        };

        $scope.pager = PaginationService.createPager(10);

        $scope.sortBy = function (column) {
            if ($scope.sortColumn === column) {
                $scope.reverse = !$scope.reverse; // Đảo thứ tự nếu cùng cột
            } else {
                $scope.sortColumn = column;
                $scope.reverse = false; // Mặc định tăng dần khi đổi cột
            }
            $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
        };

        $scope.loadAll();
        $scope.getStatusClass();
        $rootScope.$on('searchResults/orders', function(event, items) {
            $timeout(function() {
                $scope.items = items.length ? items : [];
                console.log("Dữ liệu sau khi cập nhật:", $scope.items);
                $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
            });

        });

    });
})();