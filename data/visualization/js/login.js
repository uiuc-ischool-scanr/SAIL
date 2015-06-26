var accessToken;
function statusChangeCallback(response) {
  console.log('statusChangeCallback');
  console.log(response);
  // The response object is returned with a status field that lets the
  // app know the current login status of the person.
  // Full docs on the response object can be found in the documentation
  // for FB.getLoginStatus().
  if (response.status === 'connected') {
    // Logged into your app and Facebook.
    testAPI();
    getgroupList();
    accessToken = response.authResponse.accessToken;
    console.log("accessToken" + accessToken);
  } else if (response.status === 'not_authorized') {
    // The person is logged into Facebook, but not your app.
    document.getElementById('status').innerHTML = 'Please log ' +
      'into this app.';
  } else {
    // The person is not logged into Facebook, so we're not sure if
    // they are logged into this app or not.
    document.getElementById('status').innerHTML = 'Please log ' +
      'into Facebook.';
    jQuery("#picture").attr("src", "");
    $('#glist')
    .find('option')
    .remove();
    //location.reload();
  }
}

// This function is called when someone finishes with the Login
// Button.  See the onlogin handler attached to it in the sample
// code below.
function checkLoginState() {
  console.log("Checking login status");
  FB.getLoginStatus(function (response) {
    statusChangeCallback(response);
  });
}

window.fbAsyncInit = function () {
  FB.init({
    appId : '711755018906767',
    cookie : true, // enable cookies to allow the server to access
    // the session
    xfbml : true, // parse social plugins on this page
    version : 'v2.1' // use version 2.1
  }, {
    scope : 'groups'
  });
  FB.getLoginStatus(function (response) {
    statusChangeCallback(response);
  });
};

// Load the SDK asynchronously
(function (d, s, id) {
  var js,
  fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id))
    return;
  js = d.createElement(s);
  js.id = id;
  js.src = "//connect.facebook.net/en_US/sdk.js";
  fjs.parentNode.insertBefore(js, fjs);
}
  (document, 'script', 'facebook-jssdk'));

// Here we run a very simple test of the Graph API after login is
// successful.  See statusChangeCallback() for when this call is made.
function testAPI() {
  console.log('Welcome!  Fetching your information.... ');
  FB.api('/me?fields=id,name,picture', function (response) {
    console.log('Successful login for: ' + response.name);
    console.log('Successful login for: ' + response.id);
    console.log(response.picture.data.url);
    document.getElementById('status').innerHTML =
      'Thanks for logging in, ' + response.name;
    document.getElementById('picture').src = response.picture.data.url;
  });
}

//append each of the groups to a drop down list
function displayGroups(response) {
  console.log('displayGroups');
  console.log(response);
  console.log(response.data.length);
  for (var i = 0; i < response.data.length; i++) {
    var opt = document.createElement("option");
    opt.value = response.data[i].id;
    opt.innerHTML = response.data[i].name; // whatever property it has
    // then append it to the select element
    glist.appendChild(opt);
  }

}

//this function gets json having all the group names and IDs
function getgroupList() {
  console.log('Welcome!  Fetching your groups information.... ');
  FB.api('/me/groups', function (response) {
    console.log('Successful retrieved data');
    if (!response || response.error) {
      document.getElementById('group').innerHTML =
        'Looks like you are not part of any Facebook Groups!! SORRY!!';
    } else {
      document.getElementById('group').innerHTML =
        'Choose any one of these groups for the visualization';
      displayGroups(response);
    }
  });
}

//sends the access token and the groupID of the group chosen by the user
function getID(group_ID) {
  console.log('getID' + group_ID);
  console.log("accessToken" + accessToken);
  var http = new XMLHttpRequest();
  var url = "dashboard.html";
  var params = "accessToken=" + accessToken + "&group_ID=" + group_ID;
  console.log("params" + params);
  http.open("GET", url + "?" + params, true);
  http.send();
}

var $_GET = {};
function getREQUEST(variable) {
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i = 0; i < vars.length; i++) {
    var pair = vars[i].split("=");
    $_GET[pair[0]] = pair[1];
    //if(pair[0] == variable){return pair[1];}
  }
  //return(false);
}
getREQUEST();
console.log($_GET);

function cleanData(data) {
  // body...
  for (var i = 0; i < data.length; i++) {
    if ('caption' in data[i]) {
      data[i]['type'] = 'link';
    }
    if (!('caption' in data[i])) {
      data[i]['caption'] = '';
    }
    if (!('comments' in data[i])) {
      data[i]['comments'] = [];
    }
    if (!('likes' in data[i])) {
      data[i]['likes'] = [];
    }
  };
  return data;
}

$_DATA = [];

function getAllData(response) {
  // body...
  console.log(response);
  if (!('paging' in response) || response.paging.next == null) {
    console.log("Got all data. Now drawing the chart.");
    console.log($_DATA);
    d3.selectAll("#graphs > svg").remove();
    uids = null;
    drawViz(null, $_DATA);
    return true;
  }
  FB.api(response.paging.next, function (response) {
    console.log('Successful retrieved data');
    if (!response || response.error) {
      console.log("There was some error getting the group post data.");
      console.log(response);
    } else {
      console.log("Great we got the data.");
      console.log(response.data.length);
      $_DATA = $_DATA.concat(cleanData(response.data));
      getAllData(response);

    }
  });

}

function getGroupData(gid, g) {
  // body...
  console.log(g);
  //$('#switchlines').trigger('click');
  $('#group-name').text($("#glist option:selected").text());
  $_DATA = [];
  query = gid + '/feed?fields=from,message,caption,likes,comments{from,message,created_time,like_count,comment_count},created_time,id,type';
  res = {
    paging : {
      next : query
    }
  };
  console.log(res);
  getAllData(res);

}