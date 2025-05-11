(function () {
    var app = angular.module('app');
    app.controller('HomeController', function($scope, $http) {
        $scope.selectedYear = new Date().getFullYear();
        var host = 'http://localhost:8080';

        const today = new Date();
        const lastWeek = new Date();
        lastWeek.setDate(today.getDate() - 7);

        $scope.startDate = lastWeek;
        $scope.endDate = today;

        let barChart;

        $scope.loadRevenueByDateRange = function () {
            if (!$scope.startDate || !$scope.endDate) {
                alert("Vui lòng chọn đầy đủ ngày bắt đầu và kết thúc");
                return;
            }

            const start = $scope.startDate.toISOString().split('T')[0];
            const end = $scope.endDate.toISOString().split('T')[0];

            $http.get(`${host}/revenue/by-category?startDate=${start}&endDate=${end}`)
                .then(response => {
                    $scope.revenueData = response.data;
                    $scope.labels = $scope.revenueData.map(item => item[0]);
                    $scope.data = $scope.revenueData.map(item => item[1]);

                    if (barChart) barChart.destroy();

                    const ctx = document.getElementById('myChart').getContext('2d');
                    barChart = new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: $scope.labels,
                            datasets: [{
                                label: 'Doanh thu theo Category',
                                data: $scope.data,
                                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                                borderColor: 'rgba(75, 192, 192, 1)',
                                borderWidth: 1
                            }]
                        },
                        options: {
                            responsive: true,
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    title: {
                                        display: true,
                                        text: 'Doanh thu (VNĐ)'
                                    }
                                },
                                x: {
                                    title: {
                                        display: true,
                                        text: 'Loại sản phẩm'
                                    }
                                }
                            }
                        }
                    });
                })
                .catch(error => {
                    console.error("Lỗi khi load doanh thu theo ngày:", error);
                });
        };

        $scope.loadQuantityData = function() {
            $http.get(`${host}/revenue/quantity-by-category`)
                .then(function(response) {
                    // Chuyển đổi dữ liệu API thành dữ liệu cho biểu đồ
                    $scope.labels = response.data.map(function(item) {
                        return item[0]; // Tên loại sản phẩm
                    });
                    $scope.data = response.data.map(function(item) {
                        return item[1]; // Tổng số lượng sản phẩm bán ra
                    });

                    // Vẽ biểu đồ
                    var ctxDonut = document.getElementById('donutChart').getContext('2d');
                    var donutChart = new Chart(ctxDonut, {
                        type: 'doughnut',
                        data: {
                            labels: $scope.labels,
                            datasets: [{
                                label: 'Số lượng',
                                data: $scope.data, // Số lượng sản phẩm theo loại
                                backgroundColor: [
                                    'rgb(75, 192, 192)', // Xanh (Trái cây)
                                    'rgb(255, 99, 132)', // Đỏ (Rau củ)
                                    'rgb(66, 1, 12)', // Đỏ đậm (Nấm)
                                    'rgb(29,214,5)',
                                    'rgb(214,186,5)'
                                ],
                                hoverOffset: 10
                            }]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: {
                                    position: 'top',
                                }
                            }
                        }
                    });
                })
                .catch(function(error) {
                    console.log('Error loading quantity data:', error);
                });
        };

        $scope.loadMonthlyRevenue = function () {
            $http.get(`${host}/revenue/monthly`)
                .then(function (resp) {
                    const labels = resp.data.map(item => `Tháng ${item[0]}`);
                    const data = resp.data.map(item => item[1]);

                    const ctx = document.getElementById('monthlyRevenueLineChart').getContext('2d');
                    new Chart(ctx, {
                        type: 'line',
                        data: {
                            labels: labels,
                            datasets: [{
                                label: 'Doanh thu theo tháng (VNĐ)',
                                data: data,
                                fill: false,
                                borderColor: 'rgb(75, 192, 192)',
                                backgroundColor: 'rgb(75, 192, 192)',
                                tension: 0.3, // smooth or straight line (0 = straight)
                                pointBackgroundColor: 'white',
                                pointBorderColor: 'rgb(75, 192, 192)',
                                pointRadius: 5
                            }]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: {
                                    display: true,
                                    position: 'top',
                                },
                                tooltip: {
                                    mode: 'index',
                                    intersect: false,
                                }
                            },
                            scales: {
                                x: {
                                    title: {
                                        display: true,
                                        text: 'Tháng'
                                    }
                                },
                                y: {
                                    beginAtZero: true,
                                    title: {
                                        display: true,
                                        text: 'Doanh thu (VNĐ)'
                                    }
                                }
                            }
                        }
                    });
                })
                .catch(error => {
                    console.error("Lỗi lấy doanh thu theo tháng:", error);
                });
        };

        $scope.loadMonthlyRevenue();
        $scope.loadQuantityData();
        $scope.loadRevenueByDateRange();

    });

})();