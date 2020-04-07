const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const actionTypeNewLike = "new_like";
const actionTypeNewComment = "new_comment";
const actionTypeNewPost = "new_post";
const notificationTitle = "Social App";

const followingPosDbValue = "following_post";
const followingPosDbKey = "followingPostsIds";
const followingsDbKey = "followings";
const followersDbKey = "followers";
const followingDbKey = "follow";

const postsTopic = "postsTopic";

const THUMB_MEDIUM_SIZE = 1024; //px
const THUMB_SMALL_SIZE = 100; //px

const THUMB_MEDIUM_DIR = "medium";
const THUMB_SMALL_DIR = "small";

const gcs = require('@google-cloud/storage');
const path = require('path');
const sharp = require('sharp');
const os = require('os');
const fs = require('fs');
exports.helloWorld = function.database.ref()