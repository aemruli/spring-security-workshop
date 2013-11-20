#Enabling method security
1. Add a `<security:global-method-security/>` configuration tag and enable the secured annotations
2. Add `@org.springframework.security.access.annotation.Secured` Annotations with a role name to the methods of the `SimpleAccountManager`. The role name must start with `ROLE_
3. Verify that the test fails with *An Authentication object was not found in the SecurityContext*
4. Create a `@org.junit.Before` Method inside the jUnit Test and authenticate the user by
    - Creating a empty `SecurityContext` with `SecurityContextHolder.createEmptyContext();`
    - Setting a org.springframework.security.authentication.TestingAuthenticationToken` to the context
    - Binding the context with `org.springframework.security.core.context.SecurityContextHolder#setContext` method
5. Do not forget to create a `@org.junit.After` method which clears the `SecurityContext`
6. Add a `<security:authentication-manager/>` element into your code
7. Refactor the `@Before` method to use the `org.springframework.security.authentication.AuthenticationManager` to authenticate before setting the `SecurityContext`
8. Run the test an notice the exception *No AuthenticationProvider found for org.springframework.security.authentication.TestingAuthenticationToken*
9. Configure a `org.springframework.security.authentication.TestingAuthenticationProvider`
10. Reference to the newly created `TestingAuthenticationProvider` inside `<security:authentication-manager/>` with a nested `security:authentication-provider` element
11. Run the test and verify that it is green


# Verifying password
1. Add a new `<security:user-service/>` element and created one user with a password and the specific role as nested `<security:user/>` element
2. Name the  `<security:user-service/>` with an id, which will be used later
3. Replace the `<security:authentication-provider ref=".."/>` attribute with an `<security:authentication-provider user-service-ref=".."/>` attribute
4. Provide the bean name to your `<security:user-service/>` bean
5. Run the test and notice a *No AuthenticationProvider found for org.springframework.security.authentication.TestingAuthenticationToken*
6. Re-write your `@Before` method to use a `org.springframework.security.authentication.UsernamePasswordAuthenticationToken`
7. Verify that the test runs
8. *OPTIONAL*: You can change the password (which is different to the configured user) and see that a *Bad Credentials* exception is thrown

# Encoding password
1. Add a `<security:password-encoder hash="md5"/>` to the `<security:authentication-provider/>` as a nested element
2. Run the test and recognize the *Bad Credentials* exception
3. Replace your password with a md5 hashed one (e.g. *5ebe2294ecd0e0f08eab7690d2a6ee69* instead of *secret* )
4. Re-run the test and verify the green bar
5. Add a new element `<security:salt-source system-wide="verySecret"/>` as a sub element of `<security:password-encoder hash="md5"/>`
6. Re-run the test and recognize the *Bad Credentials* exception
7. Create a md5 (e.g. online) for the password with the salt `password{salt}` (e.g. *secret{verySecret}* has a md5 of *69819676c786fc8d36f1365e7cf07d2c*) and set that as a password for your user
8. Change the `<security:salt-source />` to use a user-property ("username") instead of a system-wide salt
9. Re-run the test and see them failing with a *Bad Credentials* exception
10. Change the hash to match your username and salt (e.g. for the user *agim* the the md5 source is *secret{agim}* which is *dadacb850ece4991a835177831d1fb35*)
11. Re-run the test and see the green bar

# Using custom user details service implementation
1. Create an own instance of the `org.springframework.security.core.userdetails.UserDetailsService` interface
2. Inside that class implement the method `loadUserByUsername` to return a `org.springframework.security.core.userdetails.User` for the given user name (Do not forget to return the encoded password )
3. Annotate your implementation with a `org.springframework.stereotype.Component` annotation
4. Remove the `<security:user-service/>` and replace the `user-service-ref` attribute inside the `security:authentication-provider` with your bean name
5. Re-run the test and notice the green bar (If one test runs and the other fails, then please ensure to return a copy of the User for each request, because credentials are erased after authentication)

# Using LDAP with Spring
1. First add the following dependencies to the maven pom and refresh your project
    - `org.apache.directory.server:apacheds-server-jndi:1.5.5`
    - `org.springframework.security:spring-security-ldap:${spring-security-version}`
    - `org.slf4j:slf4j-jcl:1.5.6`

2. We need to populate our ldap server with the user to register, therefore open `src/main/resources/account/users.ldif` and add the following user definition
`dn: uid=<myUser>,ou=people,dc=mimacom,dc=com`
`objectclass: top`
`objectclass: person`
`objectclass: organizationalPerson`
`objectclass: inetOrgPerson`
`uid: <myUser>`
`userPassword: <myPassword>`
3. Replace `<myUser>` (2xtimes) and `<myPassword>` with your values, please ensure to user the same user that you have used before
4. Open the `/account/accounts-config.xml` and add an  `<security:ldap-server/>` element with an `id`, the reference to the ldif file and the following `root` attribute value `"dc=mimacom,dc=com"`
5. Replace your authentication provider with an `ldap-authentication-provider` element
6. Add a server reference to the `<security:ldap-server/>` server
7. Run the tests, you should now get a *Access is denied*. This because the user does not have any roles in the LDAP
8. Open  `src/main/resources/account/users.ldif` and insert the following group definition (after your user)

`dn: cn=<myRole>,ou=groups,dc=mimacom,dc=com`
`objectClass: groupOfNames`
`objectClass: top`
`cn: <myRole>`
`description: Application users`

9. Add your user to the definition by adding the following line after the group description
`uniquemember: uid=<myUser>,ou=people,dc=mimacom,dc=com`
10. Replace `<myUser>` with your user id created before
11. Replace `<myRole>`  with the role name in the `@Secured` annotation (e.g. if you have `ROLE_USER` replace it with `USER`)
12. Run the test and notice the green bar

# Working with authorities
1. Restore your configuration to use the UserDetailsService and remove all LDAP specific configuration files (replace `ldap-authentication-provider` with
    `<security:authentication-provider user-service-ref="simpleUserDetailsService">
           <security:password-encoder hash="md5">
               <security:salt-source user-property="username"/>
           </security:password-encoder>
       </security:authentication-provider>`)
2. Re-run your test to ensure that you have a green bar
3. Add a new role to the `account.internal.SimpleAccountManager#storeAccount` method (e.g. you should have two roles `@Secured({"ROLE_USER", "ROLE_ADMIN"})`)
4. Run the test and ensure that you have a green bar
5. Create a new `org.springframework.security.access.vote.AffirmativeBased` bean and add a `org.springframework.security.access.vote.RoleVoter` to the constructor argument
6. In the `<security:global-method-security/>` element set the attribute `access-decision-manager-ref` as a reference to the previously created access decision manager
7. Run the test and ensure that you have a green bar
8. Replace the `org.springframework.security.access.vote.AffirmativeBased` bean class with a `org.springframework.security.access.vote.UnanimousBased`
9. Run the test an notice the red bar with the Access is denied exception
10. Go into you `org.springframework.security.core.userdetails.UserDetailsService` implementation and give the user both roles that you specified in the store method
11. Re-run the test and notice the green bar

# Working with expressions
1. Remove your custom access decision manager created before
2. Enable the `org.springframework.security.access.prepost.PreAuthorize` and `org.springframework.security.access.prepost.PostAuthorize` in the `<security:global-method-security/>` element
3. Replace the `org.springframework.security.access.annotation.Secured` annotation with `org.springframework.security.access.prepost.PreAuthorize` annotation
4. Run the tests to ensure a green bar
5. Add a new field called `owner` to the `account.domain.Account` class of type `String`, the owner will be the user name who is allowed to load and store the accounts
6. Add a `org.springframework.security.access.prepost.PostAuthorize` annotation to the `account.internal.SimpleAccountManager#getById` method. Check if the returned object it's owner field equals to the username (Hint: the return object is available through the key word `returnObject` and the current user through the key word `authentication`)
7. Extends the `org.springframework.security.access.prepost.PreAuthorize` to also evaluate that the `account` parameter has the same owner as the current user. You can retrieve the parameter through the `#account` keyword.
8. Run the test and ensure a green bar
9. Play around by setting a different user name to the Account object then the logged in user, you should recognize an *Access Denied* exception


# Web Security
1. Add the following maven dependencies to use spring security in a web environment
     `<dependency>
         <groupId>org.springframework</groupId>
         <artifactId>spring-web</artifactId>
         <version>${spring-framework-version}</version>
     </dependency>
     <dependency>
         <groupId>org.springframework.security</groupId>
         <artifactId>spring-security-web</artifactId>
          <version>${spring-security-version}</version>
      </dependency>`
2.  Open the file `src/main/webapp/WEB-INF/web.xml` and add a new listener with the class name `org.springframework.web.context.ContextLoaderListener`
3.  Create a spring configuration file `/src/main/webapp/WEB-INF/applicationContext.xml` and import the file `classpath:/account/accounts-config.xml`
4.  Start the application, and you should see rendered `index.jsp` on your browser
5.  Add a `<security:http/>` element to the config file and an automated form login by adding a `<security:form-login/>` element as a sub-element
6.  Open the `src/main/webapp/WEB-INF/web.xml` and add a new filter with the name `springSecurityFilterChain` and the class `org.springframework.web.filter.DelegatingFilterProxy`
7.  Map the filter to intercept all the request with the pattern /*
8.  Open the `src/main/webapp/WEB-INF/web.xml` and secure the url /* to be only accessed by a valid role
9.  Re-start your server and login into your application


# Web Security with certificates
1.- Create a key store for the server by calling the java key tool with the following command
`keytool -genkeypair -alias serverkey -keyalg RSA -dname "CN=Web Server,OU=Hosting Company,O=mimacom,L=Stuttgart,S=BW,C=DE" -keystore server.jks`

2. Create a key store for the client by calling the java key tool with the following command
`keytool -genkeypair -alias clientkey -keyalg RSA -dname "CN=agim,OU=Development,O=mimacom,L=Stuttgart,S=BW,C=DE" -keystore client.jks`

3. Now export public key of the client (which will be imported on the server) by issueing the following command
`keytool -exportcert -alias clientkey -file client-public.cer -keystore client.jks`

4. Now import the client certificate to the server it’s trust store (so that the server can verify the private key of the client)
`keytool -importcert -keystore server.jks -alias clientcert -file client-public.cer`

5. Now export the public certificate from the server for the client browser du verify the server it’s identity
`keytool -exportcert -alias serverkey -file server-public.cer -keystore server.jks`

6. Now import the client server certificate to the client trust store, in case you want to use a java client
`keytool -importcert -keystore client.jks -alias servercert -file server-public.cer`

7. Configure the SSL connector for tomcat and point to your `server.jks` file and configure the password for the key store. You can configure the connector in the server.xml inside the conf directory of your tomcat instance. The definition for a container looks like this

` <Connector
                protocol="HTTP/1.1"
                port="8443" maxThreads="20"
                scheme="https" secure="true" SSLEnabled="true"
                keystoreFile="<yourPath>/server.jks" keystorePass=„<yourPass>"
                truststoreFile=„<yourPath>/server.jks" truststorePass="<yourPass>"
                clientAuth=„false" sslProtocol="TLS" />`

8. Next start the web application and use the link [https://localhost:8080], add the (unsigned) certificate to your browser key store
9. Next open the file `src/main/webapp/WEB-INF/applicationContext.xml` and replace the `<security:form-login/>` element with a
   `<security:x509 />`
10. Add a `user-service-ref` element to your user details service
11. Next you need to export the key store certificate which is Java specific to an open key file. Please run the following command to export your private key into a browser readable format
`keytool -importkeystore -srckeystore client.jks -destkeystore client-private.p12 -srcstoretype jks -deststoretype pkcs12`
12. Next we need to enable the client authentication with certificates for the ssl connector. Therefore open the file `server.xml` in the conf folder of your and enable the client authentication by setting the clientAuth attribute to true in your config.
13. Restart your tomcat and call [http://localhost:8080/] and provide the exported certificate
14. You should now see your application

# Now you have finished your course