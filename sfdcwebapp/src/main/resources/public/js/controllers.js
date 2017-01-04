/*
 * JS file for all of the Angular controllers in the app
 */
'use strict';

/*
 * Define the SFDC controllers scope for Angular
 */
var sfdcControllers = angular.module('sfdcControllers', []);

sfdcApp.controller('AccountListController', function($scope, $http, $interval) {
	
	$scope.getAccounts = function() {
		$scope.getAccountServiceStatus();
		// when landing on the page, get all accounts and show them
		$http.get('/accounts').success(function(data) {
			console.log("response data: " + JSON.stringify(data));
			$scope.accounts = data;
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};
	
	//Handles the delete request function
	$scope.delete = function(accountId) {
		//console.log("deleting id " + accountId);
		$http.delete('/account/'+accountId).success(function(data) {
			$scope.getAccounts();
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};

	$scope.getAccountServiceStatus = function() {
		// when landing on the page, get all accounts and show them
		$http.get('/manage/health').success(function(data) {
			console.log("response data: " + JSON.stringify(data));
			var serviceStatus = JSON.stringify(data.discoveryComposite.discoveryClient.services);
			console.log("serviceStatus: " + serviceStatus);
			$scope.isAccountServiceUP = serviceStatus.search("accountservice") != -1 ? true : false;
			console.log("isAccountServiceUP: " + serviceStatus.search("accountservice") != -1 ? true : false);
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};
	
	$interval($scope.getAccountServiceStatus(), 10000);
		
	// Initial page load
	$scope.getAccounts();
});

sfdcApp.controller('EditAccountInfoController', function($scope, $http, $routeParams, $interval) {
	//Handles the update request function
	$scope.update = function(account) {
		console.log("updating account: "+JSON.stringify(account));
		if(!(account.Id) && $routeParams.id == "new") {
			$http.post("/account/new", account).success(function(data, status, headers, config) {
				console.log("created account: "+status);
				console.log("created account: "+data);
				$scope.account = data;
				$scope.message = "Successfully saved the account.";
			}).error(function(data, status, headers, config) {
				$scope.message = "";
				$scope.error = "There was an error saving the account.";
			});
		} else {
			var accountId = account.Id;
			$http.put("/account/" + accountId, account).success(function(data, status, headers, config) {
				$scope.message = "Successfully saved the account.";
				$http.get("/account/" + accountId).success(function(data) {
					$scope.account = data;
					$scope.message = "Successfully saved the account.";
					$scope.error = "";
				});
			}).error(function(data, status, headers, config) {
				$scope.message = "";
				$scope.error = "There was an error saving the account.";
			});			
		}
	};
	
	//Handles the delete request function of the account
	$scope.delete = function(id) {
		if($routeParams.id != "new") {
			$http.delete('/account/'+id).success(function(data) {
				$scope.account = "";
				$scope.message = "Successfully deleted the account.";
				$scope.error = "";
			});
		}
	}

	//Handles the reset request function and the initial load of the entry
	$scope.reset = function() {
		if($routeParams.id != "new") {
			$http.get("/account/" + $routeParams.id).success(function(data) {
				$scope.account = data;
			});
		}
	}

	$scope.getAccountServiceStatus = function() {
		// when landing on the page, get all accounts and show them
		$http.get('/manage/health').success(function(data) {
			console.log("response data: " + JSON.stringify(data));
			var serviceStatus = JSON.stringify(data.discoveryComposite.discoveryClient.services);
			console.log("serviceStatus: " + serviceStatus);
			$scope.isAccountServiceUP = serviceStatus.search("accountservice") != -1 ? true : false;
			console.log("isAccountServiceUP: " + serviceStatus.search("accountservice") != -1 ? true : false);
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};
	
	$interval($scope.getAccountServiceStatus(), 10000);
	
	//Initial load
	$scope.reset();
});

sfdcApp.controller('OppAcctListController', function($scope, $http, $interval) {
	$scope.getAccounts = function() {
		$scope.getAccountServiceStatus();
		$http.get('/opp_by_accts').success(function(data) {
			console.log("response data: " + JSON.stringify(data));
			$scope.accounts = data;
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};
	$scope.getAccountServiceStatus = function() {
		// when landing on the page, get all accounts and show them
		$http.get('/manage/health').success(function(data) {
			console.log("response data: " + JSON.stringify(data));
			var serviceStatus = JSON.stringify(data.discoveryComposite.discoveryClient.services);
			console.log("serviceStatus: " + serviceStatus);
			$scope.isAccountServiceUP = serviceStatus.search("accountservice") != -1 ? true : false;
			console.log("isAccountServiceUP: " + serviceStatus.search("accountservice") != -1 ? true : false);
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};
	
	$interval($scope.getAccountServiceStatus(), 10000);

	// Initial page load
	$scope.getAccounts();
});

sfdcApp.controller('AccountInfoController', function($scope, $http, $routeParams, $interval) {
	$scope.getAccount = function() {
		$scope.getAccountServiceStatus();
		$http.get("/account/" + $routeParams.id).success(function(data) {
			$scope.account = data;
		});				
	};

	//Handles the delete request function of the account
	$scope.delete = function(id) {
		if($routeParams.id != "new") {
			$http.delete('/account/'+id).success(function(data) {
				$scope.account = "";
				$scope.message = "Successfully deleted the account.";
				$scope.error = "";
			});
		}
	}

	$scope.getAccountServiceStatus = function() {
		// when landing on the page, get all accounts and show them
		$http.get('/manage/health').success(function(data) {
			console.log("response data: " + JSON.stringify(data));
			var serviceStatus = JSON.stringify(data.discoveryComposite.discoveryClient.services);
			console.log("serviceStatus: " + serviceStatus);
			$scope.isAccountServiceUP = serviceStatus.search("accountservice") != -1 ? true : false;
			console.log("isAccountServiceUP: " + serviceStatus.search("accountservice") != -1 ? true : false);
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};
	
	$interval($scope.getAccountServiceStatus(), 10000);
	// Initial page load
	$scope.getAccount();
});

sfdcApp.controller('EditContactInfoController', function($scope, $http, $routeParams, $interval) {
	//Handles the update request function
	$scope.update = function(contact) {
		console.log("updating contact: "+JSON.stringify(contact));
		if(!(contact.Id) && $routeParams.id == "new") {
			contact.AccountId = $routeParams.accountId;
			$http.post("/contact/new", contact).success(function(data, status, headers, config) {
				console.log("created contact: "+status);
				console.log("created contact: "+data);
				$scope.contact = data;
				$scope.message = "Successfully saved the contact.";
				$scope.error = "";
			}).error(function(data, status, headers, config) {
				$scope.message = data.data;
				$scope.error = "There was an error saving the contact.";
			});
		} else {
			var contactId = contact.Id;
			$http.put("/contact/" + contactId, contact).success(function(data, status, headers, config) {
				$scope.message = "Successfully saved the contact.";
				$http.get("/contact/" + contactId).success(function(data) {
					$scope.contact = data;
					$scope.message = "Successfully saved the contact.";
					$scope.error = "";
				});				
			}).error(function(data, status, headers, config) {
				$scope.message = "";
				$scope.error = "There was an error saving the contact.";
			});			
		}
	};
	
	//Handles the delete request function and the initial load of the entry
	$scope.delete = function(id) {
		if($routeParams.id != "new") {
			$http.delete("/contact/" + id).success(function(data) {
				$scope.contact = "";
				$scope.message = "Successfully deleted the contact.";
				$scope.error = "";
			});
		}
	}

	//Handles the reset request function and the initial load of the entry
	$scope.reset = function() {
		if($routeParams.id != "new") {
			$http.get("/contact/" + $routeParams.id).success(function(data) {
				$scope.contact = data;
			});
		}
	}

	$scope.getContactServiceStatus = function() {
		// when landing on the page, get all accounts and show them
		$http.get('/manage/health').success(function(data) {
			console.log("response data: " + JSON.stringify(data));
			var serviceStatus = JSON.stringify(data.discoveryComposite.discoveryClient.services);
			console.log("serviceStatus: " + serviceStatus);
			$scope.isContactServiceUP = serviceStatus.search("contactservice") != -1 ? true : false;
			console.log("isContactServiceUP: " + serviceStatus.search("contactservice") != -1 ? true : false);
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};
	
	$interval($scope.getContactServiceStatus(), 10000);
	
	//Initial load
	$scope.reset();
});

sfdcApp.controller('ContactInfoController', function($scope, $http, $routeParams, $interval) {
	$scope.getContact = function() {
		$scope.getContactServiceStatus();
		$http.get('/contact/'+$routeParams.id).success(function(data) {
			//console.log("response data: " + JSON.stringify(data));
			$scope.contact = data;
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};

	//Handles the delete request function and the initial load of the entry
	$scope.delete = function(id) {
		if($routeParams.id != "new") {
			$http.delete("/contact/" + id).success(function(data) {
				$scope.contact = "";
				$scope.message = "Successfully deleted the contact.";
				$scope.error = "";
			});
		}
	}
	
	$scope.getContactServiceStatus = function() {
		// when landing on the page, get all accounts and show them
		$http.get('/manage/health').success(function(data) {
			console.log("response data: " + JSON.stringify(data));
			var serviceStatus = JSON.stringify(data.discoveryComposite.discoveryClient.services);
			console.log("serviceStatus: " + serviceStatus);
			$scope.isContactServiceUP = serviceStatus.search("contactservice") != -1 ? true : false;
			console.log("isContactServiceUP: " + serviceStatus.search("contactservice") != -1 ? true : false);
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};
	
	$interval($scope.getContactServiceStatus(), 10000);

	// Initial page load
	$scope.getContact();
});

sfdcApp.controller('EditOpportunityInfoController', function($scope, $http, $routeParams, $interval) {
	//Handles the update request function
	$scope.update = function(opportunity) {
		console.log("updating opportunity: "+JSON.stringify(opportunity));
		if(!(opportunity.Id) && $routeParams.id == "new") {
			opportunity.AccountId = $routeParams.accountId;
			$http.post("/opportunity/new", opportunity).success(function(data, status, headers, config) {
				console.log("created opportunity: "+status);
				console.log("created opportunity: "+data);
				$scope.opportunity = data;
				$scope.message = "Successfully saved the opportunity.";
				$scope.error = "";
			}).error(function(data, status, headers, config) {
				$scope.message = "";
				$scope.error = "There was an error saving the opportunity.";
			});
		} else {
			var opportunityId = opportunity.Id;
			$http.put("/opportunity/" + opportunityId, opportunity).success(function(data, status, headers, config) {
				$scope.message = "Successfully saved the opportunity.";
				$http.get("/opportunity/" + opportunityId).success(function(data) {
					$scope.opportunity = data;
					$scope.message = "Successfully saved the opportunity.";
					$scope.error = "";
				});				
			}).error(function(data, status, headers, config) {
				$scope.message = "";
				$scope.error = "There was an error saving the opportunity.";
			});			
		}
	};
	
	//Handles the delete request function and the initial load of the opportunity
	$scope.delete = function(id) {
		if($routeParams.id != "new") {
			$http.delete("/opportunity/" + id).success(function(data) {
				$scope.opportunity = "";
				$scope.message = "Successfully deleted the opportunity.";
				$scope.error = "";
			});
		}
	}

	//Handles the reset request function and the initial load of the opportunity
	$scope.reset = function() {
		if($routeParams.id != "new") {
			$http.get("/opportunity/" + $routeParams.id).success(function(data) {
				$scope.opportunity = data;
				$scope.opportunity.CloseDate = new Date($scope.opportunity.CloseDate);
			});
		}
	}

	$scope.getOpportunityServiceStatus = function() {
		// when landing on the page, get all accounts and show them
		$http.get('/manage/health').success(function(data) {
			console.log("response data: " + JSON.stringify(data));
			var serviceStatus = JSON.stringify(data.discoveryComposite.discoveryClient.services);
			console.log("serviceStatus: " + serviceStatus);
			$scope.isOpportunityServiceUP = serviceStatus.search("opportunityservice") != -1 ? true : false;
			console.log("isOpportunityServiceUP: " + serviceStatus.search("opportunityservice") != -1 ? true : false);
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};
	
	$interval($scope.getOpportunityServiceStatus(), 10000);
	
	//Initial load
	$scope.reset();
});

sfdcApp.controller('OpportunityInfoController', function($scope, $http, $routeParams, $interval) {
	$scope.getOpportunity = function() {
		$scope.getOpportunityServiceStatus();
		$http.get('/opportunity/'+$routeParams.id).success(function(data) {
			//console.log("response data: " + JSON.stringify(data));
			$scope.opportunity = data;
//			$scope.opportunity.CloseDate = new Date($scope.opportunity.CloseDate);
//			$scope.opportunity.CloseDate.format = "MM/DD/YYYY";
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};

	//Handles the delete request function and the initial load of the opportunity
	$scope.delete = function(id) {
		if($routeParams.id != "new") {
			$http.delete("/opportunity/" + id).success(function(data) {
				$scope.opportunity = "";
				$scope.message = "Successfully deleted the opportunity.";
				$scope.error = "";
			});
		}
	}

	$scope.getOpportunityServiceStatus = function() {
		// when landing on the page, get all accounts and show them
		$http.get('/manage/health').success(function(data) {
			console.log("response data: " + JSON.stringify(data));
			var serviceStatus = JSON.stringify(data.discoveryComposite.discoveryClient.services);
			console.log("serviceStatus: " + serviceStatus);
			$scope.isOpportunityServiceUP = serviceStatus.search("opportunityservice") != -1 ? true : false;
			console.log("isOpportunityServiceUP: " + serviceStatus.search("opportunityservice") != -1 ? true : false);
		}).error(function(data) {
			console.log('Error: ' + data);
			$scope.message = data.message;
			$scope.error = data.code;
		});
	};
	
	$interval($scope.getOpportunityServiceStatus(), 10000);
	
	// Initial page load
	$scope.getOpportunity();
});
