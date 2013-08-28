from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String

engine = create_engine('sqlite:///foo.db', echo=True)
Session = sessionmaker(bind=engine)
session = Session()

Base = declarative_base()
class User(Base):
	__tablename__ = 'user'

	id = Column(Integer, primary_key=True)
	name = Column(String)
	fullname = Column(String)
	password = Column(String)

	def __init__(self, name, fullname, password):
		self.name = name
		self.fullname = fullname
		self.password = password

	def __repr__(self):
		return "<User('%s', '%s', '%s')>" % (self.name, self.fullname, self.password)

Base.metadata.create_all(engine)

session.add_all([
	User('1ed', '1Ed jones', '1edpassword'),
	User('2ed', '2Ed jones', '2edpassword'),
	User('3ed', '3Ed jones', '3edpassword')])
session.commit()

query = session.query(User)
