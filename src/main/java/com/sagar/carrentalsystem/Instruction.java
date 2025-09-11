/***
 * The Complete Flow of Admin Creation
 *
 * The entire process involves three distinct requests and a single database seed, all working together.
 *
 * Step 1: The AdminSeeder Runs (One-time Event)
 *
 * This is the very first step, which happens automatically when your application starts for the first time.
 *
 * Action: Your AdminSeeder CommandLineRunner checks the database.
 *
 * Logic: It verifies if an admin user with the initialAdminEmployeeId ("ADMIN1") already exists.
 *
 * Outcome: Since this is the first run, it won't find the user. It then creates a new User entity with the hardcoded credentials (email: admin@test.com, employee ID: "ADMIN1", role: ADMIN) and saves it to the users table. The password is saved as an encoded hash. No token is involved at this stage, as this is a server-side process, not a user-initiated request.
 *
 * The system now has a single, pre-configured admin account.
 *
 * Step 2: The Initial Admin Logs In (Request 1)
 *
 * This is the first interaction a real person has with the system to get a JWT.
 *
 * Endpoint: POST /api/auth/login
 *
 * Request Body: The client sends the credentials for the initial admin: {"email":"admin@test.com", "password":"adminpassword", "role":"ADMIN", "employeeId":"ADMIN1"}.
 *
 * Process:
 *
 * The AuthenticationManager uses your CustomUserDetailsService to find the user by email in the database.
 *
 * It compares the provided plain-text password (adminpassword) with the stored hashed password.
 *
 * Your code then performs an extra check to ensure the provided role (ADMIN) and employeeId (ADMIN1) match the ones in the database.
 *
 * If all checks pass, the user is authenticated.
 *
 * Your JwtGenerator takes the authentication object, extracts the user details (including the ADMIN role), and generates a JWT.
 *
 * Outcome: The loginUser endpoint returns a 200 OK response with the newly generated JWT. This token is the key that grants access to protected endpoints.
 *
 * Step 3: The Initial Admin Registers a New Admin (Request 2)
 *
 * This is the action you were attempting, but it requires the token from the previous step.
 *
 * Endpoint: POST /api/admin/register
 *
 * Headers: The client must include the JWT obtained in Step 2 in the Authorization header, like this: Authorization: Bearer <your_JWT_token>.
 *
 * Request Body: The client sends the new admin's details, including a valid employee ID that exists in your valid_employees table (e.g., {"email":"sagarAdmin@gmail.com", ..., "employeeId":"EMP12345"}).
 *
 * Process:
 *
 * The JwtAuthFilter intercepts the request. It extracts the JWT from the Authorization header.
 *
 * It validates the token's signature, expiration, and issuer.
 *
 * If valid, it sets the user's authentication context, allowing the request to proceed. This is where your SecurityConfig rule .requestMatchers("/api/admin/**").hasRole("ADMIN") is applied.
 *
 * The request reaches your AdminController. The @PreAuthorize annotation performs a final check to ensure the authenticated user has the ADMIN role.
 *
 * Your registerNewAdmin method then executes its logic: it verifies the new employeeId against the ValidEmployeeRepository and creates the new user account in the users table.
 *
 * Outcome: A 200 OK response confirms that a new admin account has been created, and the new admin can now log in using the same process as in Step 2.
 */






/**
 * Question : Can you help me understand what happens when a login request comes either admin or customer what will be called first jwtAuthfilter or
 * spring secuirty config and what happens in subsequent preotect api callss. since login is a public call when it calls jwt auth filter here we
 * put a condition a called token is not null so what will be checked next and what in case of protect apis so how the permitall helps explain details by taking an exaple
 */

/***
 * Answer
 *When a login request arrives, the JwtAuthFilter is called first, but it allows the request to proceed to the AuthController because there is no JWT present. In subsequent protected API calls, the filter requires a token. The permitAll() rule in SecurityConfig is a key part of this process.
 *
 * Login Request Flow (Public)
 *
 * Request Arrival: A POST request to /api/auth/login is sent.
 *
 * JwtAuthFilter: This is the very first filter in your security chain. It checks the Authorization header for a JWT. Since a login request doesn't have a token yet, the filter's condition if (StringUtils.hasText(token)) is false. It logs "JWT token is not present" and then calls filterChain.doFilter(request, response). This allows the request to continue down the filter chain.
 *
 * SecurityConfig Rule: The request reaches your SecurityConfig's rules. The line .requestMatchers("/api/auth/**").permitAll() matches the URL. This rule tells Spring Security to bypass all further authorization checks for this request.
 *
 * AuthController: The request is now allowed to reach your AuthController.loginUser() method. You authenticate the user's credentials, generate a JWT, and return it to the client.
 *
 * Essentially, for a public endpoint, the JwtAuthFilter is active but doesn't block the request, and the SecurityConfig's permitAll() rule ensures that the request can complete without needing a token.
 *
 * Protected API Call Flow
 *
 * Request Arrival: A GET request to a protected endpoint like /api/admin/dashboard is sent. The Authorization header includes a JWT.
 *
 * JwtAuthFilter: The filter runs first. It finds the JWT in the header. The condition if (StringUtils.hasText(token)) is now true. It extracts the user's email from the token, loads their details from CustomUserDetailsService, and validates the token. If valid, it sets the user's authentication in the SecurityContextHolder.
 *
 * SecurityConfig Rule: The request, now with an authenticated user in the security context, reaches your SecurityConfig rules. The line .requestMatchers("/api/admin/**").hasRole("ADMIN") matches the URL. Spring Security checks the roles of the authenticated user.
 *
 * Authorization Check:
 *
 * If ADMIN: The user's role is ADMIN, which matches the required role. The request is authorized and proceeds to the controller.
 *
 * If CUSTOMER: The user's role is CUSTOMER. The authorization check fails, and the request is blocked, returning a 403 Forbidden error.
 *
 * The permitAll() rule is crucial because it allows specific endpoints to be accessed by anyone, circumventing the authentication and authorization checks that are applied to all other protected endpoints. This is how you allow a user to log in and register without first needing a token.
 */


/***
 * The line .anyRequest().authenticated() acts as a catch-all rule. It means that any request whose URL path hasn't been matched by a previous rule must be authenticated.
 */


/***
 * Subsequent protect API Requests
 * Question : So incase of protected apis after jwtAuth fileter step again will they checked against UsernamePasswordAuthenticationFilter CustomUserDetailsService? If they hav
 *
 */

/***
 * Answer
 * No, once a request has a valid token, it won't go through the UsernamePasswordAuthenticationFilter or CustomUserDetailsService again. The JwtAuthFilter handles the entire authentication process for protected APIs.
 *
 * How It Works
 *
 * JwtAuthFilter is the Entry Point: Your JwtAuthFilter runs first for every request.
 *
 * If a request has a token, the JwtAuthFilter takes over. It validates the token's signature, expiration, and claims.
 *
 * Crucially, if the token is valid, the filter uses the information from the token to create a UsernamePasswordAuthenticationToken and sets it directly in the SecurityContextHolder. This is where the authenticated user is "placed" for the rest of the request.
 *
 * Bypassing Other Filters: Once the JwtAuthFilter successfully authenticates the user and sets the SecurityContextHolder, the request proceeds down the filter chain. Other filters, including the UsernamePasswordAuthenticationFilter, will see that the SecurityContextHolder already contains an authenticated user and will do nothing. They will simply let the request pass.
 */

/**
 * Explanation of Handlers:
 *
 * EntityNotFoundException: Handles cases where an entity (like a car or booking) is not found in the database. Returns a 404 Not Found.
 *
 * IllegalStateException: Catches logical errors, like trying to pay for a booking that isn't APPROVED. Returns a 400 Bad Request.
 *
 * IllegalArgumentException: Handles invalid data passed to a service, such as a mismatched car variant or invalid dates. Returns a 400 Bad Request.
 *
 * SecurityException: Catches unauthorized actions, such as a customer trying to cancel another customer's booking. Returns a 403 Forbidden.
 *
 * MethodArgumentNotValidException: Specifically handles validation errors that occur due to the @Valid annotation on your DTOs. It extracts all validation messages and returns a structured 400 Bad Request response.
 *
 * Exception.class: This is a general handler that catches any other unexpected exceptions. It prevents internal server errors from exposing sensitive details to the client and instead returns a generic 500 Internal Server Error.
 */