defmodule ApiWeb.Router do
  use ApiWeb, :router

  pipeline :api do
    plug CORSPlug, [origin: "*"]
    plug :accepts, ["json"]
  end

  scope "/api", ApiWeb do
    pipe_through :api

    resources "/posts", PostController, only: [:show]
    resources "/images", ImageController, only: [:show]
  end
end
