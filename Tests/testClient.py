#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Simple HTTP server that tests the client.
Usage::
	./testClient.py [<port>]
"""
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import SocketServer
import json
import sys
import thread

lastUpdate = ""
lastBigUpdate = ""

class RequestHandler(BaseHTTPRequestHandler):
	scheduled = {}
	lastUpdate = ""
	lastBigUpdate = ""

	def _set_headers(self):
		self.send_response(200)
		self.send_header('Content-Type', 'application/json')
		self.end_headers()

	def do_GET(self):
		pass

	def do_HEAD(self):
		self._set_headers()
	   
	def parseRequest(self, data):
		if ("select" in self.path):
			response = json.dumps({"0" : "npc_dota_hero_lina"})
			return response
		elif "levelup" in self.path:
			response = json.dumps({"abilityIndex" : 1})
			return response

		if "server" in self.path and "update" in self.path:
			RequestHandler.lastBigUpdate = data
		#   print data
		#   print "<----------------REQUEST END------------------>"
			
		if "client" in self.path and "update" in self.path:
			RequestHandler.lastUpdate = data
			if RequestHandler.scheduled: 
				response = json.dumps(RequestHandler.scheduled)
				RequestHandler.scheduled = {}
				print("Sending scheduled. " + response)
				return response
		
		#   return json.dumps({"command" : "NOOP"})

		return ""

	def do_POST(self):
		#if "server" in self.path and "update" in self.path:
		#   print "<----------------REQUEST START ----------------->"
		#   print self.path
		#   print self.headers

		request_headers = self.headers
		content_length = request_headers.getheaders('Content-Length')
		length = int(content_length[0]) if content_length else 0

		post_data = self.rfile.read(length)

		self._set_headers()
		self.wfile.write(self.parseRequest(post_data))
		self.send_response(200)

	def log_message(self, format, *args):
		return

 	@staticmethod
	def schedule(cmd):
		RequestHandler.scheduled = cmd

 	@staticmethod
	def printUpdate():
		print RequestHandler.lastUpdate
 	
 	@staticmethod
	def printBigUpdate():
		print RequestHandler.lastBigUpdate

class ServerRunner: 
	httpd = []

	def runServer(self, server_class=HTTPServer, handler_class=RequestHandler, port=8080):
		server_address = ('', port)
		httpd = server_class(server_address, handler_class)
		print 'Starting httpd on address and port: ', server_address
		httpd.serve_forever()

	def startServer(self):
		thread.start_new_thread(self._startServer, ())
		
	def _startServer(self):
		from sys import argv
		if len(argv) == 2:
			self.runServer(port=int(argv[1]))
		else:
			self.runServer()

	def run(self):
		while True: 
			txt = raw_input(">")
			if "quit" in txt:
				sys.exit(0)
			elif "help" in txt:
				print "print world -> prints the last client update"
				print "print bigworld -> prints the last big update"
			elif "print world" in txt:
				RequestHandler.printUpdate()
			elif "print bigworld" in txt:
				RequestHandler.printBigUpdate()
			elif "pickup" in txt:
				cmd = {"target" : int(txt.split(" ")[1]),
					"command" : "PICKUP_RUNE"}
				RequestHandler.schedule(cmd)
			elif "buy" in txt:
				cmd = {"command" : "BUY",
					"item" : txt.split(" ")[1]}
				RequestHandler.schedule(cmd)
			elif "graball" in txt:
				cmd = {"command" : "GRAB_ALL"}
				RequestHandler.schedule(cmd)
			else:
				_txt = txt.replace("'", "\"")
				cmd = json.loads(_txt)
				RequestHandler.schedule(cmd)
				



if __name__ == "__main__":
	runner = ServerRunner()
	runner.startServer()
	runner.run()


	





