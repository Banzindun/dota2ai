#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json


class ServerRequest:
	header = {}
	msg = ""
	addr = ""
	method = "POST"

	def __init__(self):
		self.header['Content-type'] = 'application/json'
		self.header['Accept'] = "application/json"
		self.header["X-Jersey-Tracing-Threshold"] = "VERBOSE"

		self.addr = 'http://localhost:8080/Dota2AI'
		self.method = "POST"

	def setEndURL(self, addr):
		self.addr += addr

	def msgFromJSON(self, data):
		self.msg = json.dumps(data)

	def msgFromString(self, data):
		self.msg = data

	def setMethod(self, method):
		self.method = method




class SampleRequests:
	def select(self):
		req = ServerRequest()
		req.setEndURL("/server/select")

		return req

	def botsSelected(self):
		req = ServerRequest()
		req.setEndURL("/server/botsselected")

		data = {"index" : "0", "id" : "123"}
		req.msgFromJSON(data)

		return req


