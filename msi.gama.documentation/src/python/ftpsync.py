# -*- coding: iso-8859-1 -*-
"""
    StatWiki - FTP synchronizer

    @copyright: 2009 Arkadiusz Wahlig
    @license: GNU GPL, see COPYING for details.
"""


import ftplib
import os
import sys

import config
import wikiutil

# Third party library, see:
# http://ftputil.sschwarzer.net/
import ftputil


class FTPSession(ftplib.FTP):
    '''Session for ftputil.FTPHost class. Supports additional port and passive arguments.
    '''
    
    def __init__(self, host, user='', password='', account='', port=0, passive=True):
        ftplib.FTP.__init__(self)
        self.connect(host, port)
        self.login(user, password, account)
        self.set_pasv(passive)


class SynchronizerBase(object):
    '''Synchronizer framework.
    '''
    
    def __init__(self, host):
        self.host = host
        try:
            host.synchronize_times()
        except ftputil.TimeShiftError:
            pass

    def on_init(self):
        pass
        
    def on_exit(self):
        pass
        
    def on_directory(self, name):
        return True
        
    def on_file(self, name):
        return True
        
    # Upload files and directories that does not exist on FTP or are older than local copies.
    def subsync(self, dirname):
        try:
            self.host.chdir(dirname)
        except ftputil.PermanentError:
            self.host.mkdir(dirname)
            self.host.chdir(dirname)
        os.chdir(dirname)
        try:
            for name in os.listdir('.'):
                if os.path.isdir(name):
                    if self.on_directory(name) and self.recursive:
                        self.subsync(name)
                elif os.path.isfile(name):
                    if self.on_file(name):
                        for try_ in xrange(3):
                            try:
                                ok = self.host.upload_if_newer(name, name, 'b')
                            except Exception, err:
                                try:
                                    self.host.unlink(name)
                                except:
                                    pass
                            else:
                                if ok:
                                    print 'uploaded %s' % \
                                        os.path.abspath(name)[len(self.root):].lstrip(os.sep)
                                break
                        else:
                            raise err
                else:
                    raise OSError('local %s is not a file or directory' % name)
        finally:
            if dirname != '.':
                os.chdir('..')
                self.host.chdir('..')

    # Remove files and directories from FTP that do not exist locally.
    def subclean(self, dirname):
        if os.path.exists(dirname) and self.on_directory(dirname):
            self.host.chdir(dirname)
            os.chdir(dirname)
            try:
                for name in [x for x in self.host.listdir('.') if not x.startswith('.')]:
                    if self.host.path.isdir(name):
                        if self.recursive:
                            self.subclean(name)
                    elif self.host.path.isfile(name):
                        if not os.path.exists(name) or not self.on_file(name):
                            relpath = os.path.abspath(name)[len(self.root):].lstrip(os.sep)
                            if self.clean:
                                print 'removing %s' % relpath
                                self.host.unlink(name)
                            else:
                                print >>sys.stderr, 'warning: %s' % (self.clean_error % relpath)
                    else:
                        raise OSError('remote %s is not a file or directory' % name)
            finally:
                if dirname != '.':
                    os.chdir('..')
                    self.host.chdir('..')
        else:
            relpath = os.path.abspath(dirname)[len(self.root):].lstrip(os.sep) + os.sep
            if self.clean:
                print 'removing %s' % relpath
                self.host.rmtree(dirname, ignore_errors=True)
            else:
                print >>sys.stderr, 'warning: %s' % (self.clean_error % relpath)

    def synchronize(self, recursive=True, clean=False, clean_error='%s has no local copy'):
        self.recursive = recursive
        self.clean = clean
        self.clean_error = clean_error
        self.root = os.getcwd()
        self.on_init()
        self.subsync('.')
        self.subclean('.')
        self.on_exit()


class Synchronizer(SynchronizerBase):
    '''Populated synchronizer.'
    '''
    
    def on_directory(self, name):
        return not name.startswith('_')
        
    def on_file(self, name):
        if name.startswith('_') or name.endswith('.wiki'):
            return False
        dir = os.getcwd()
        path = os.path.join(dir, name)
        if path == wikiutil.fixFileNameCase(os.path.join(self.root, os.path.normcase(config.filename))) or \
                path == wikiutil.fixFileNameCase(os.path.join(self.root, os.path.normcase(config.general.generator))):
            return False
        # Accept the file for upload / Reject from removal.
        return True
        

# Synchronizes current directory with configured FTP server.
def synchronize(clean=False):
    try:
        host = config.ftp.host
    except AttributeError:
        sys.exit('cannot synchronize, configure the FTP server access first')

    username = getattr(config.ftp, 'username', '')
    password = getattr(config.ftp, 'password', '')

    ftp = ftputil.FTPHost(host, username, password,
        port=int(config.ftp.port), passive=int(config.ftp.passive),
        session_factory=FTPSession)
    
    path = getattr(config.ftp, 'path', '')
    if path:
        ftp.chdir(path)

    syncer = Synchronizer(ftp)
    syncer.synchronize(clean=clean, clean_error='%s has no local copy, use --force to remove')
