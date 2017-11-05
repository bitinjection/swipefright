defmodule ApiWeb.ImageController do
  use ApiWeb, :controller

  alias Api.Content
  alias Api.Content.Image

  action_fallback ApiWeb.FallbackController

  def index(conn, _params) do
    images = Content.list_images()
    render(conn, "index.json", images: images)
  end

  def create(conn, %{"image" => image_params}) do
    with {:ok, %Image{} = image} <- Content.create_image(image_params) do
      conn
      |> put_status(:created)
      |> put_resp_header("location", image_path(conn, :show, image))
      |> render("show.json", image: image)
    end
  end

  def show(conn, %{"id" => id}) do
    image = Content.get_image!(id)
    render(conn, "show.json", image: image)
  end

  def update(conn, %{"id" => id, "image" => image_params}) do
    image = Content.get_image!(id)

    with {:ok, %Image{} = image} <- Content.update_image(image, image_params) do
      render(conn, "show.json", image: image)
    end
  end

  def delete(conn, %{"id" => id}) do
    image = Content.get_image!(id)
    with {:ok, %Image{}} <- Content.delete_image(image) do
      send_resp(conn, :no_content, "")
    end
  end
end
