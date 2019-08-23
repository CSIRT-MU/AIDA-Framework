from __future__ import absolute_import # optional, but I like it
from .common import *

# Production overrides
DEBUG = False
SECURE_CONTENT_TYPE_NOSNIFF = True
SECURE_BROWSER_XSS_FILTER = True
SECURE_SSL_REDIRECT = True
SESSION_COOKIE_SECURE = True
CSRF_COOKIE_SECURE = True
CSRF_COOKIE_HTTPONLY = True
X_FRAME_OPTIONS = 'DENY'
SECURE_HSTS_SECONDS = 'localhost'
SECURE_HSTS_INCLUDE_SUBDOMAINS = True
# Disable admin on production
ADMIN_ENABLED = False

