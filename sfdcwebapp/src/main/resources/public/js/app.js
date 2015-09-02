var sfdcApp = angular.module('sfdcwebapp', [ 'ngRoute',
		'sfdcControllers' ]);

sfdcApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/accounts', {
		templateUrl : '/partials/accountList.html',
		controller : 'AccountListController'
	}).when('/account/:id', {
		templateUrl : '/partials/accountInfo.html',
		controller : 'AccountInfoController'
	}).when('/account/e/:id', {
		templateUrl : '/partials/editAccount.html',
		controller : 'EditAccountInfoController'
	}).when('/opp_by_accts', {
		templateUrl : '/partials/oppAcctList.html',
		controller : 'OppAcctListController'
	}).when('/contact/:id', {
		templateUrl : '/partials/contactInfo.html',
		controller : 'ContactInfoController'
	}).when('/contact/:accountId/:id', {
		templateUrl : '/partials/editContact.html',
		controller : 'EditContactInfoController'
	}).when('/opportunity/:id', {
		templateUrl : '/partials/opportunityInfo.html',
		controller : 'OpportunityInfoController'
	}).when('/opportunity/:accountId/:id', {
		templateUrl : '/partials/editOpportunity.html',
		controller : 'EditOpportunityInfoController'
	}).otherwise({
		redirectTo : '/accounts'
	})
} ])
