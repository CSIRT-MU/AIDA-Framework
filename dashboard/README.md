# Dashboard

Dashboard aims to be universal web application for data visualisation with user management and
built-in system of [pages and widgets](https://gitlab.ics.muni.cz/CSIRT-MU/dashboard/wikis/home#panels-and-widgets).

## Getting Started

* [Installation instructions](https://gitlab.ics.muni.cz/CSIRT-MU/dashboard/wikis/Getting-started)
* [Deployment](https://gitlab.ics.muni.cz/CSIRT-MU/dashboard/wikis/Deployment)

**For developers**: When you start implementing new features, create a branch in the project, not a fork. Naming conventions:
  * `bachelor-thesis/<name-surname>` when the work is a part of your bachelor thesis
  * `master-thesis/<name-surname>` when the work is a part of your master thesis
  * `<project_name>/<topic>` when the work is a part of some project
  * When you are doing some quick fixes and need a separate branch, append the `wip/` prefix.
  * For more details, see existing branches.
  
## Built With

* [Django](https://www.djangoproject.com/) - The web framework used for the back-end
* [Django REST](http://www.django-rest-framework.org/) - Library for RESTful API
* [pip](https://pypi.python.org/pypi/pip) - Dependency manager for Python
* [Angular](https://angular.io/) - The web framework used for the front-end
* [npm](https://docs.npmjs.com/) - Dependency manager for JavaScript and TypeScript
* [Angular CLI](https://cli.angular.io/) - A command line interface for Angular


## Authors

* **Lukáš Matta** (445679@mail.muni.cz) - *Lead Developer*
* **Matej Šípka** - RT + ADS + newsfeed [[DP, 2018](https://is.muni.cz/auth/th/ant0e/)]
* **Anna Marie Dombajová** - DNS firewall [BP, 2018-]
* **Michal Pavúk** - grafové vizualizace (CRUSOE) [2018-]
* **Zoltán Strcuľa** - time series data (iTOA) [DP, 2018-]
* **Jakub Kolman** - SABU dashboard customization [BP, 2018-2019]

### Alumni
* **David Beran** - Initial work

See also the list of [contributors](https://gitlab.ics.muni.cz/CSIRT-MU/dashboard/graphs/master) who participated in this project.

## License

MIT License

## Acknowledgments


<!-- # Dashboard

Used technologies:
* Python 3.6, Django 1.11 for back-end
* Angular 4.2 for front-end

Front-end is stored in folder `frontend`. Angular is used, so run project using CLI. You need [npm](https://nodejs.org/en/) to use Angular.

```
cd frontend
npm install
ng serve
```

## Vagrant
You don't have to install Python to your local development environment. All you need is [Vagrant](https://www.vagrantup.com/) installed.

```
vagrant up
```

Now check that you can see Angular app on [http://localhost:8080](http://localhost:8080). If you get eror 403 (Forbidden), it means you probably do not have Angular app builded (folder `frontend/dist` is empty or does not exist). To fix this, build your app.
```
cd frontend
ng build
```

Also if you initialize Django project (see below), you should see Django app on [http://localhost:8000](http://localhost:8000). If Django is not used (folder `backend` is empty), then you should see error 403 (Forbidden), which is correct behavior.

## Django
Recommended way to initialize and use Django in your project is to use prepared Vagrant box and virtualenv inside it.

```
vagrant up
vagrant ssh
```

You should see `(django-venv)` at the begining of your bash prompt, which means virtualenv is activated. If you don't, then activate it manually.

```
source ~/django-venv/bin/activate
```

Now you are ready to create your Django project (replace `PROJECT_NAME` with name of your project). Don't forget that dot at the end - really important!

```
cd /srv/backend
django-admin startproject PROJECT_NAME .
```

Now edit `vagrant/playbook.yml` - find line starting with `backend_app:` and replace the text with `backend_app: /srv/backend/PROJECT_NAME`.

### Django database
Create migration and admin user.
```
cd /srv/backend
python manage.py migrate
python manage.py createsuperuser
```

### Static files
Generate Django static files.
```
cd /srv/backend
python manage.py collectstatic
```

Add following line into `backend/PROJECT_NAME/PROJECT_NAME/settings.py`.

```python
STATIC_ROOT = BASE_DIR + "/static/"
```

### pip packages
It is important NOT to install pip packages manually in your vagrant box nor your local environment. Use ansible to do it for you and anybody else working on this project. Simply add package name and version into `backend/requirements.txt` file. This is common Python practice.

## Notes
You have some neat aliases at hand inside vagrant box. Feel free to add some more
```
alias cdb="cd /srv/backend"
alias django="python manage.py"
```

HACK! Sqlite database file may not be writable for both vagrant and apache. You can solve it by
```
chmod g+w /srv/backend/db.sqlite3
```

HACK! you have tu manually reload Apache (or provision Vagrant box) after backend code changes to apply them by `sudo service apache2 reload`. TODO fix this! 

```
## Mock REST api
you can globaly install json-server from https://github.com/typicode/json-server and run server that helps simulate calls
to get external REST api containing  mock data, that is stored in frontend/src/mockdata/db.json
to start up json server you need to run
cd .\frontend\
npm run json:server
it will set up local json-server on localhost:3000

-->
