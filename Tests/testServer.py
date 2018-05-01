#!/usr/bin/env python
# -*- coding: utf-8 -*-

import requests
import json

from ServerRequests import SampleRequests

class ServerTester:
	scheduled = []

	def __init__(self):
		pass

	def run(self):
		for r in self.scheduled:
			self.send(r)

	def send(self, req):
		if (req.method == "POST"):
			r = requests.post(req.addr, data=req.msg, headers=req.header)
			print r.content
		else:
			pass
			# TODO
	def schedule(self, req):
		self.scheduled.append(req)





def main():
	st = ServerTester()

	sq = SampleRequests()

	st.schedule(sq.select())
	st.schedule(sq.botsSelected())

	st.run()

if __name__ == "__main__":
	main()